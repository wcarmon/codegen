package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.getKotlinImportsForFields
import com.wcarmon.codegen.model.util.kotlinMethodArgsForFields

/**
 * Kotlin related convenience methods for a [Entity]
 */
class KotlinEntityView(
  private val entity: Entity,
  private val jvmView: JVMEntityView,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isKotlin) {
      "invalid target language: $targetLanguage"
    }
  }

  val kotlinImportsForFields: Set<String> = getKotlinImportsForFields(entity)

  val kotlinInsertPreparedStatementSetterStatements by lazy {
    buildInsertPreparedStatementSetterStatements(targetLanguage)
  }

  val kotlinUpdatePreparedStatementSetterStatements by lazy {
    buildUpdatePreparedStatementSetterStatements(TargetLanguage.KOTLIN_JVM_1_4)
  }

  val kotlinPreparedStatementSetterStatementsForPK by lazy {
    buildPreparedStatementSetterStatementsForPK(TargetLanguage.KOTLIN_JVM_1_4)
  }

  fun kotlinUpdateFieldPreparedStatementSetterStatements(field: Field) =
    buildUpdateFieldPreparedStatementSetterStatements(field, TargetLanguage.KOTLIN_JVM_1_4)

  fun kotlinMethodArgsForPrimaryKeyFields(qualified: Boolean) =
    kotlinMethodArgsForFields(primaryKeyFields, qualified)

  //TODO: more here
}
