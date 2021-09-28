package com.wcarmon.codegen.view

import com.wcarmon.codegen.UPDATED_TS_FIELD_NAMES
import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.util.*

// For aligning columns
private const val CHARS_FOR_COLUMN_NAME = 20
private const val CHARS_FOR_COLUMN_TYPE = 12
private const val CHARS_FOR_DEFAULT_CLAUSE = 13
private const val CHARS_FOR_NULLABLE_CLAUSE = 10


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

  val isUpdatedTimestamp: Boolean =
    field.effectiveBaseType(SQL_POSTGRESQL).isTemporal &&
        UPDATED_TS_FIELD_NAMES.any {
          field.name.lowerCamel.equals(it, true)
        }

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
    if (!isUpdatedTimestamp && fieldForUpdateTimestamp != null) {

      val wrappedFieldRead = WrapWithSerdeExpression(
        serde = fieldForUpdateTimestamp.effectiveRDBMSSerde(targetLanguage),
        serdeMode = SerdeMode.SERIALIZE,
        wrapped = RawLiteralExpression("clock.instant()"),
      )

      setterExpressions += PreparedStatementSetNonNullExpression(
        columnIndex = currentColumnIndex,
        fieldReadExpression = wrappedFieldRead,
        setterMethod = defaultPreparedStatementSetterMethod(
          fieldForUpdateTimestamp.effectiveBaseType(JAVA_08)
        ),
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
            .terminated
        )
      }
  }

  /**
   * Works on PostgreSQL, H2, Maria, MySQL, DB2
   * (maybe oracle too, but they make it near impossible to test)
   *
   * Builds a complete column definition (For 1 column)
   *
   * Trailing commas must be handled by the caller (eg. [RDBMSTableView])
   *
   * See https://www.postgresql.org/docs/current/sql-createtable.html
   *
   * @return Sub-expression, part of `CREATE TABLE` statement
   *   Something like "<field-name> <field-type> <nullability> <default value>"
   */
  val postgresqlColumnDefinition: String by lazy {
    buildColumnDefinition(SQL_POSTGRESQL)
  }

  /**
   * Works directly on SQLite (without using their affinity conversion layer)
   *
   * See https://www.sqlite.org/lang_createtable.html
   * See https://www.sqlite.org/lang_createtable.html#tablecoldef
   */
  val sqliteColumnDefinition: String by lazy {
    buildColumnDefinition(SQL_SQLITE)
  }

  private fun buildColumnDefinition(dbLanguage: TargetLanguage): String {
    check(dbLanguage.isSQL)

    val parts = mutableListOf<String>()

    parts += "\"${field.name.lowerSnake}\""
      .padEnd(CHARS_FOR_COLUMN_NAME, ' ')

    parts += field.effectiveTypeLiteral(dbLanguage)
      .padEnd(CHARS_FOR_COLUMN_TYPE, ' ')

    // -- nullable clause
    val nullableClause =
      if (!field.type.nullable) "NOT NULL"
      else ""

    parts += nullableClause.padEnd(CHARS_FOR_NULLABLE_CLAUSE, ' ')

    // -- default clause
    val defaultClause = DefaultValueExpression(field).render(renderConfig)
    parts += defaultClause.padEnd(CHARS_FOR_DEFAULT_CLAUSE, ' ')

    return parts.joinToString(" ")
  }
}
