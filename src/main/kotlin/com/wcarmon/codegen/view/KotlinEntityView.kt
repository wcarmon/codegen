package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.model.BaseFieldType.LIST
import com.wcarmon.codegen.model.BaseFieldType.SET
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.JDBCColumnIndex.Companion.FIRST
import com.wcarmon.codegen.model.PreparedStatementBuilderConfig
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.KOTLIN_JVM_1_4
import com.wcarmon.codegen.util.buildPreparedStatementSetters
import com.wcarmon.codegen.util.getKotlinImportsForFields
import com.wcarmon.codegen.util.kotlinMethodArgsForFields

/**
 * Kotlin related convenience methods for a [Entity]
 */
class KotlinEntityView(
  debugMode: Boolean,
  private val entity: Entity,
  private val jvmView: JVMEntityView,
  private val rdbmsView: RDBMSTableView,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isKotlin) {
      "invalid target language: $targetLanguage"
    }
  }

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    targetLanguage = targetLanguage,
    terminate = false
  )

  //TODO: supporting suffix is hard from freemarker
  fun documentation(
    vararg prefix: String,
  ) = DocumentationExpression(
    parts = prefix.toList() + entity.documentation,
  )
    .render(config = renderConfig)

  val importsForFields: Set<String> = getKotlinImportsForFields(entity)

  val insertPreparedStatementSetterStatements by lazy {
    rdbmsView.insertPreparedStatementSetterStatements(renderConfig)
  }

  val preparedStatementSetterStatementsForPK by lazy {

    val psConfig = PreparedStatementBuilderConfig(
      allowFieldNonNullAssertion = false,
      fieldOwner = EmptyExpression,
      fieldReadMode = DIRECT,
      preparedStatementIdentifierExpression = RawLiteralExpression("ps"),
    )

    buildPreparedStatementSetters(
      fields = entity.idFields,
      firstIndex = FIRST,
      psConfig = psConfig,
    )
      .joinToString(separator = "\n") {
        it.render(renderConfig)
      }
  }

  val updatePreparedStatementSetterStatements by lazy {
    rdbmsView.updatePreparedStatementSetterStatements(renderConfig)
  }

  val fieldDeclarations: String by lazy {

    entity.sortedFieldsWithIdsFirst
      .joinToString(
        separator = ",\n",
      ) { field ->
        field.kotlinView.fieldDeclaration
      }
  }

  val typeReferenceDeclarations: String by lazy {
    val indentation = "  "

    jvmView.collectionFields
      .joinToString("\n") { field ->
        val output = StringBuilder(512)

        val collectionLiteral = when (field.effectiveBaseType(KOTLIN_JVM_1_4)) {
          SET -> "Set"
          LIST -> "List"
          else -> TODO("Add typeref support for field=$field, entity=$entity")
        }

        val typeParameters = field.typeParameters(KOTLIN_JVM_1_4)

        //TODO: should I use field.jvmView.jacksonTypeRef
        output.append("val ${entity.name.upperSnake}__${field.name.upperSnake}_TYPE_REF")
        output.append(": TypeReference<$collectionLiteral<${typeParameters.first()}>> ")
        output.append("=\n")
        output.append(indentation)
        output.append("object : TypeReference<$collectionLiteral<${typeParameters.first()}>>() {}")

        output.toString()
      }
  }

  /**
   * setter methods on a Proto builder
   */
  val entityToProtobufSetters: String by lazy {

    entity.sortedFieldsWithIdsFirst
      .joinToString(
        separator = "\n"
      ) { field ->

        val effectiveDefaultValue = field.effectiveDefaultValue(targetLanguage)

        val read = FieldReadExpression(
          assertNonNull = false,
          fieldName = field.name,
          fieldOwner = RawLiteralExpression("entity"),
          overrideFieldReadMode = DIRECT,
        )

        val wrapMe =
          if (!field.type.nullable || effectiveDefaultValue.isAbsent) {
            read

          } else {
            DefaultWhenNullExpression(
              primaryExpression = read,
              defaultValueExpression = DefaultValueExpression(field)
            )
          }

        val wrapper = WrapWithSerdeExpression(
          serde = field.effectiveProtobufSerde(targetLanguage),
          serdeMode = SERIALIZE,
          wrapped = wrapMe,
        )

        ProtobufFieldWriteExpression(
          field = field,
          sourceReadExpression = wrapper,
        )
          .render(renderConfig.unterminated)
      }
  }

  val protobufToEntitySetters: String by lazy {
    entity.sortedFieldsWithIdsFirst
      .joinToString(
        separator = "\n",
      ) { field ->

        val read = ProtobufFieldReadExpression(
          assertNonNull = false,
          field = field,
          fieldOwner = RawLiteralExpression("proto"),
          serde = field.effectiveProtobufSerde(KOTLIN_JVM_1_4),
        )

        val renderedRead = read.render(renderConfig.unterminated)
        "${field.name.lowerCamel} = $renderedRead,"
      }
  }

  val interFieldValidationExpressions: String by lazy {

    entity
      .interFieldValidations
      .joinToString(
        separator = "\n\n"
      ) { v ->
        InterFieldValidationExpression(
          entity = entity,
          validationConfig = v,
        )
          .render(renderConfig)
      }
  }

  private val validatedFields =
    entity.sortedFieldsWithIdsFirst
      .filter {
        it.effectiveFieldValidation(targetLanguage).hasValidation
      }

  val fieldValidationExpressions: String by lazy {

    validatedFields.map { field ->
      FieldValidationExpressions(
        field = field,
        tableConstraintPrefix = "",
        validationConfig = field.effectiveFieldValidation(targetLanguage),
        validationSeparator = "\n",
      )
        .render(renderConfig.doubleIndented)
    }
      .filter { it.isNotBlank() }
      .joinToString(
        separator = "\n\n",
      )
  }

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

        lines += "const val PATCH__${entity.name.upperSnake}__${field.name.upperSnake} ="
        lines += """$indentation"UPDATE ${entity.rdbmsView.qualifiedTableName_escaped} " +"""
        lines += """$indentation"SET ${field.name.lowerSnake}=? " + """

        if (entity.updatedTimestampFieldName != null && !field.jvmView.isUpdatedTimestamp) {
          lines += """$indentation"AND ${entity.updatedTimestampFieldName.lowerSnake}=? " + """
        }

        lines += """$indentation"WHERE ${entity.rdbmsView.primaryKeyWhereClause_questionMarks}" """

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

  fun methodArgsForIdFields(qualified: Boolean) =
    kotlinMethodArgsForFields(entity.idFields, qualified)
}
