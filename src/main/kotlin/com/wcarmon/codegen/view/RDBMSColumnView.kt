package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.FieldReadExpression
import com.wcarmon.codegen.ast.PreparedStatementSetExpression
import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.JDBCColumnIndex
import com.wcarmon.codegen.model.PreparedStatementBuilderConfig
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.SQL_POSTGRESQL
import com.wcarmon.codegen.util.*

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
    cfg: PreparedStatementBuilderConfig = PreparedStatementBuilderConfig(),
  ): String {

    val fieldReadExpression = FieldReadExpression(
      assertNonNull = cfg.allowFieldNonNullAssertion,
      fieldName = field.name,
      fieldOwner = cfg.fieldOwner,
      overrideFieldReadMode = cfg.fieldReadMode,
    )

    val columnSetterStatement = PreparedStatementSetExpression(
      columnIndex = JDBCColumnIndex.FIRST,
      columnType = jdbcType(field.effectiveBaseType),
      field = field,
      fieldReadExpression = fieldReadExpression,
      preparedStatementIdentifierExpression = cfg.preparedStatementIdentifierExpression,
      setterMethod = defaultPreparedStatementSetterMethod(field.effectiveBaseType),
    )

    val pk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = idFields,
      firstIndex = JDBCColumnIndex(2),
    )

    return (listOf(columnSetterStatement) + pk)
      .joinToString(separator = "\n") {
        it.render(
          renderConfig.copy(targetLanguage))
      }
  }

}
