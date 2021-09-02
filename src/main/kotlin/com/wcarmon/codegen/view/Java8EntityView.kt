package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.buildJavaPreconditionStatements
import com.wcarmon.codegen.util.commaSeparatedJavaMethodArgs
import com.wcarmon.codegen.util.javaImportsForFields

/**
 * Java related convenience methods for a [Entity]
 */
class Java8EntityView(
  private val debugMode: Boolean,
  private val entity: Entity,
  private val jvmView: JVMEntityView,
  private val rdbmsView: RDBMSTableView,
  private val targetLanguage: TargetLanguage = TargetLanguage.JAVA_08,
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
    rdbmsView.insertPreparedStatementSetterStatements(targetLanguage)
  }

  val primaryKeyPreconditionStatements =
    buildJavaPreconditionStatements(entity.idFields)
      .joinToString("\n\t")

  val preparedStatementSetterStatementsForPK by lazy {
    rdbmsView.preparedStatementSetterStatementsForPrimaryKey(
      targetLanguage = targetLanguage)
  }

  val updatePreparedStatementSetterStatements by lazy {
    rdbmsView.updatePreparedStatementSetterStatements(targetLanguage)
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


  fun methodArgsForIdFields(qualified: Boolean) =
    commaSeparatedJavaMethodArgs(entity.idFields, qualified)
}
