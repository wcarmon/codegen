package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.model.*
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

  /**
   * Build & Render [PreparedStatement] setters for updating 1 field
   * Include setting the update timestamp when [fieldForUpdateTimestamp] available
   *
   * @param idFields - for the WHERE clause
   * @param targetLanguage - for rendering
   */
  fun updateFieldPreparedStatementSetterStatements(
    idFields: List<Field>,
    targetLanguage: TargetLanguage,
    fieldForUpdateTimestamp: Field? = null,
    psConfig: PreparedStatementBuilderConfig = PreparedStatementBuilderConfig(),
  ): String {

    require(field.canUpdate) { "field must be updatable: $field" }
    require(idFields.isNotEmpty()) { "id fields required for update: $field" }

    val setterExpressions = mutableListOf<Expression>()
    var currentColumnIndex = JDBCColumnIndex.FIRST

    // -- Add Field setter
    setterExpressions += buildPreparedStatementSetter(
      columnIndex = currentColumnIndex,
      field = field,
      psConfig = psConfig,
    )

    currentColumnIndex = currentColumnIndex.next()

    // -- (Conditionally) Add setter for updateTimestamp
    if (!field.isUpdatedTimestamp && fieldForUpdateTimestamp != null) {

      val wrappedFieldRead = WrapWithSerdeExpression(
        serde = effectiveJDBCSerde(fieldForUpdateTimestamp),
        serdeMode = SerdeMode.SERIALIZE,
        wrapped = RawLiteralExpression("clock.instant()"),
      )

      setterExpressions += PreparedStatementSetNonNullExpression(
        columnIndex = currentColumnIndex,
        fieldReadExpression = wrappedFieldRead,
        setterMethod = defaultPreparedStatementSetterMethod(fieldForUpdateTimestamp.effectiveBaseType),
        preparedStatementIdentifierExpression = psConfig.preparedStatementIdentifierExpression,
        // TODO: termination
      )

      currentColumnIndex = currentColumnIndex.next()
    }

    // -- PK setter (last, see WHERE clause)
    setterExpressions += buildPreparedStatementSetters(
      psConfig = psConfig,
      fields = idFields,
      firstIndex = currentColumnIndex,
    )

    // -- Combine the setters, one on each line
    return setterExpressions
      .joinToString(separator = "\n") {
        it.render(
          renderConfig
            .copy(targetLanguage = targetLanguage)
            .terminated)
      }
  }
}
