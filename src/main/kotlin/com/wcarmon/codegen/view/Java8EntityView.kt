package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.ast.FieldReadMode.GETTER
import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.JDBCColumnIndex.Companion.FIRST
import com.wcarmon.codegen.model.PreparedStatementBuilderConfig
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.*

/**
 * Java related convenience methods for a [Entity]
 */
class Java8EntityView(
  debugMode: Boolean,
  private val entity: Entity,
  private val jvmView: JVMEntityView,
  private val rdbmsView: RDBMSTableView,
  targetLanguage: TargetLanguage = TargetLanguage.JAVA_08,
) {

  init {
    require(targetLanguage.isJava) {
      "invalid target language: $targetLanguage"
    }
  }

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    lineIndentation = "",
    targetLanguage = targetLanguage,
    terminate = true,
  )

  val importsForFields: Set<String> = javaImportsForFields(entity)

  val insertPreparedStatementSetterStatements by lazy {
    rdbmsView.insertPreparedStatementSetterStatements(renderConfig)
  }

  val primaryKeyPreconditionStatements =
    buildJavaPreconditionStatements(entity.idFields)
      .joinToString("\n\t")

  val preparedStatementSetterStatementsForPK by lazy {

    val psConfig = PreparedStatementBuilderConfig(
      allowFieldNonNullAssertion = false,
      fieldOwner = EmptyExpression,
      fieldReadMode = DIRECT,
      preparedStatementIdentifierExpression = RawLiteralExpression("ps"),
    )

    //TODO: this needs termination

    buildPreparedStatementSetters(
      fields = entity.idFields,
      firstIndex = FIRST,
      psConfig = psConfig,
    )
      .joinToString(separator = "\n") {
        it.render(renderConfig.terminated)
      }
  }

  val updatePreparedStatementSetterStatements by lazy {
    rdbmsView.updatePreparedStatementSetterStatements(renderConfig)
  }

  val fieldDeclarations: String by lazy {

    entity.sortedFieldsWithIdsFirst
      .joinToString("\n") { field ->
        FieldDeclarationExpression(
          //TODO: suffix "ID/Primary key" when `field.idField`
          documentation = DocumentationExpression(field.documentation),
          finalityModifier = FinalityModifier.FINAL,
          name = field.name,
          type = field.type,
          visibilityModifier = VisibilityModifier.PRIVATE,
//      defaultValue = TODO()  TODO: fix this
        ).render(renderConfig.indented)
      }
  }


  val protoToEntitySetters: String by lazy {
    entity.sortedFieldsWithIdsFirst
      .joinToString(
        separator = "\n",
      ) { field ->

        val read = ProtoFieldReadExpression(
          assertNonNull = false,
          field = field,
          fieldOwner = RawLiteralExpression("proto"),
          serde = effectiveProtoSerde(field),
        )

        val renderedRead = read.render(renderConfig.unterminated)
        ".${field.name.lowerCamel}($renderedRead)"
      }
  }


  /**
   * setter methods on a Proto builder
   */
  val entityToProtoSetters: String by lazy {

    entity.sortedFieldsWithIdsFirst
      .joinToString(
        separator = "\n"
      ) { field ->

        val read = FieldReadExpression(
          assertNonNull = false,
          fieldName = field.name,
          fieldOwner = RawLiteralExpression("entity"),
          overrideFieldReadMode = GETTER,
        )

        val wrapper = WrapWithSerdeExpression(
          serde = effectiveProtoSerde(field),
          serdeMode = SERIALIZE,
          wrapped = read,
        )

        ProtoFieldWriteExpression(
          field = field,
          sourceReadExpression = wrapper,
        )
          .render(renderConfig.unterminated)
      }
  }

  val typeReferenceDeclarations: String by lazy {
    val indentation = "  "

    entity
      .collectionFields
      .joinToString("\n") { field ->
        val output = StringBuilder(512)

        val collectionLiteral = when (field.type.base) {
          BaseFieldType.SET -> "Set"
          BaseFieldType.LIST -> "List"
          else -> TODO("Add typeref support for field=$field, entity=$entity")
        }

        //TODO: should I use field.jvmView.jacksonTypeRef
        output.append("public static final TypeReference<$collectionLiteral<${field.type.typeParameters.first()}>> ")
        output.append("${entity.name.upperSnake}__${field.name.upperSnake}_TYPE_REF ")
        output.append("=\n")
        output.append(indentation)
        output.append("new TypeReference<>(){};")

        output.toString()
      }
  }

  val validatedFields =
    entity.sortedFieldsWithIdsFirst
      .filter {
        it.validationConfig.hasValidation
            // For Java we need validation to enforce null safety
            || !it.type.nullable
      }

  val validationExpressions: String by lazy {

    validatedFields.map { field ->
      FieldValidationExpressions(
        fieldName = field.name,
        type = field.type,
        validationConfig = field.validationConfig,
        validationSeparator = "\n"
      )
        .render(renderConfig.doubleIndented)
    }
      .filter { it.isNotBlank() }
      .joinToString(
        separator = "\n\n",
      )
  }

  //TODO: indentation
  fun patchQueries(): String {
    val indentation = "  "

    if (!entity.hasIdFields || !entity.hasNonIdFields) {
      // need ID fields for WHERE clause
      return ""
    }

    if (!entity.canUpdate) {
      return ""
    }

    return entity.nonIdFields
      .map { field ->
        val lines = mutableListOf<String>()

        lines += "public static final String PATCH__${entity.name.upperSnake}__${field.name.upperSnake} ="
        lines += """$indentation"UPDATE \"${entity.name.lowerSnake}\" " +"""
        lines += """$indentation"SET ${field.name.lowerSnake}=? " + """

        if (entity.updatedTimestampFieldName != null && !field.isUpdatedTimestamp) {
          lines += """$indentation"AND ${entity.updatedTimestampFieldName.lowerSnake}=? " + """
        }

        lines += """$indentation"WHERE ${entity.rdbmsView.primaryKeyWhereClause}";"""

        lines.joinToString(
          separator = "\n"
        ) {
          "$indentation$it"
        }

      }.joinToString(
        separator = "\n\n"
      ) {
        "$indentation$it"
      }
  }

  //TODO: accept annotations
  fun methodArgsForIdFields(qualified: Boolean) =
    commaSeparatedJavaMethodArgs(entity.idFields, qualified)
}
