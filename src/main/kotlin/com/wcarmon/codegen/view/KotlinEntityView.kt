package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.PreparedStatementBuilderConfig
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.getKotlinImportsForFields
import com.wcarmon.codegen.util.kotlinMethodArgsForFields

/**
 * Kotlin related convenience methods for a [Entity]
 */
class KotlinEntityView(
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

  val importsForFields: Set<String> = getKotlinImportsForFields(entity)

  val insertPreparedStatementSetterStatements by lazy {
    rdbmsView.insertPreparedStatementSetterStatements(targetLanguage)
  }

  val preparedStatementSetterStatementsForPK by lazy {
    rdbmsView.preparedStatementSetterStatementsForPrimaryKey(
      config = PreparedStatementBuilderConfig(),
      targetLanguage = targetLanguage,
    )
  }

  val updatePreparedStatementSetterStatements by lazy {
    rdbmsView.updatePreparedStatementSetterStatements(targetLanguage)
  }

  fun methodArgsForIdFields(qualified: Boolean) =
    kotlinMethodArgsForFields(entity.idFields, qualified)
}
