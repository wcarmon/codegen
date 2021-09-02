package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.JDBCColumnIndex
import com.wcarmon.codegen.model.PreparedStatementBuilderConfig
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.SQL_POSTGRESQL
import com.wcarmon.codegen.util.buildPreparedStatementSetter
import com.wcarmon.codegen.util.buildPreparedStatementSetters
import com.wcarmon.codegen.util.postgresColumnDefinition
import com.wcarmon.codegen.util.sqliteColumnDefinition

/**
 * RDBMS related convenience methods for a [Field]
 * See [com.wcarmon.codegen.model.RDBMSColumnConfig]
 */
class RDBMSColumnView(
  private val debugMode: Boolean,
  private val field: Field,
) {

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    targetLanguage = SQL_POSTGRESQL,
    terminate = false
  )

  /**
   * Works on PostgreSQL, H2, Maria, MySQL, DB2
   * (maybe oracle too, but they make it near impossible to test)
   */
  val postgresqlColumnDefinition = postgresColumnDefinition(field)

  /**
   * Works directly on SQLite (without using their affinity conversion layer)
   */
  val sqliteColumnDefinition = sqliteColumnDefinition(field)

  fun updateFieldPreparedStatementSetterStatements(
    idFields: List<Field>,
    targetLanguage: TargetLanguage,
    psConfig: PreparedStatementBuilderConfig = PreparedStatementBuilderConfig(),
  ): String {

    val columnSetterStatement = buildPreparedStatementSetter(
      columnIndex = JDBCColumnIndex.FIRST,
      field = field,
      psConfig = psConfig,
    )

    val pk = buildPreparedStatementSetters(
      psConfig = psConfig,
      fields = idFields,
      firstIndex = JDBCColumnIndex(2),
    )

    return (listOf(columnSetterStatement) + pk)
      .joinToString(separator = "\n") {
        it.render(
          renderConfig.copy(targetLanguage = targetLanguage))
      }
  }
}
