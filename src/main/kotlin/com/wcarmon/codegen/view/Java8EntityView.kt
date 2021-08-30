package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.DocumentationExpression
import com.wcarmon.codegen.ast.FieldDeclarationExpression
import com.wcarmon.codegen.ast.FinalityModifier
import com.wcarmon.codegen.ast.VisibilityModifier
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.buildJavaPreconditionStatements
import com.wcarmon.codegen.util.commaSeparatedJavaMethodArgs
import com.wcarmon.codegen.util.javaImportsForFields

/**
 * Java related convenience methods for a [Entity]
 */
class Java8EntityView(
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
      .map { field ->
        FieldDeclarationExpression(
          //TODO: suffix "ID/Primary key" when `field.idField`
          documentation = DocumentationExpression(field.documentation),
          finalityModifier = FinalityModifier.FINAL,
          name = field.name,
          type = field.type,
          visibilityModifier = VisibilityModifier.PRIVATE,
//      defaultValue = TODO()  TODO: fix this
        ).render(targetLanguage, true)
      }
      .joinToString("\n")
  }


  fun methodArgsForIdFields(qualified: Boolean) =
    commaSeparatedJavaMethodArgs(entity.idFields, qualified)
}
