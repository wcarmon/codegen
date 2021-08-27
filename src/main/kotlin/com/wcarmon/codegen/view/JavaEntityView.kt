package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.FieldReadMode
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.buildJavaPreconditionStatements
import com.wcarmon.codegen.model.util.commaSeparatedJavaMethodArgs
import com.wcarmon.codegen.model.util.javaImportsForFields

/**
 * Java related convenience methods for a [Entity]
 */
class JavaEntityView(
  private val entity: Entity,
  private val jvmView: JVMEntityView,
  private val targetLanguage: TargetLanguage = TargetLanguage.JAVA_08,
) {

  init {
    require(targetLanguage.isJava) {
      "invalid target language: $targetLanguage"
    }
  }

  val javaImportsForFields: Set<String> = javaImportsForFields(entity)

  val javaInsertPreparedStatementSetterStatements by lazy {
    buildInsertPreparedStatementSetterStatements(targetLanguage)
  }

  val javaUpdatePreparedStatementSetterStatements by lazy {
    buildUpdatePreparedStatementSetterStatements(targetLanguage)
  }

  val javaPrimaryKeyPreconditionStatements =
    buildJavaPreconditionStatements(primaryKeyFields)
      .joinToString("\n\t")

  val javaPreparedStatementSetterStatementsForPK by lazy {
    buildPreparedStatementSetterStatementsForPK(
      TargetLanguage.JAVA_08,
      FieldReadMode.DIRECT)
  }

  fun javaUpdateFieldPreparedStatementSetterStatements(field: Field) =
    buildUpdateFieldPreparedStatementSetterStatements(field, TargetLanguage.JAVA_08)

  fun javaMethodArgsForPrimaryKeyFields(qualified: Boolean) =
    commaSeparatedJavaMethodArgs(primaryKeyFields, qualified)

  //TODO: more here
}
