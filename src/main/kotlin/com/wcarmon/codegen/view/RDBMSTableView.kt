package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.EmptyExpression
import com.wcarmon.codegen.ast.FieldReadMode
import com.wcarmon.codegen.ast.PreparedStatementSetExpression
import com.wcarmon.codegen.ast.RawExpression
import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.util.*
import org.atteo.evo.inflector.English

/**
 * RDBMS related convenience methods for a [Entity]
 * See [com.wcarmon.codegen.model.RDBMSTableConfig]
 */
data class RDBMSTableView(
  private val entity: Entity,
) {

  val commaSeparatedColumns: String = commaSeparatedColumns(entity)

  //TODO: return Documentation
  val commentForPrimaryKeyFields: String =
    if (primaryKeyFields.isEmpty()) ""
    else "PrimaryKey " + English.plural("field", primaryKeyFields.size)

  val dbSchemaPrefix: String =
    if (entity.rdbmsConfig.schema.isBlank() != false) ""
    else "${entity.rdbmsConfig.schema}."


  val primaryKeyWhereClause: String = commaSeparatedColumnAssignment(primaryKeyFields)

  val primaryKeyTableConstraint: String = primaryKeyTableConstraint(entity)

  val questionMarkStringForInsert: String = (1..entity.fields.size).joinToString { "?" }

  val sortedFieldsWithPrimaryKeyFirst =
    primaryKeyFields + nonPrimaryKeyFields

  val updateSetClause: String = commaSeparatedColumnAssignment(nonPrimaryKeyFields)

  val commaSeparatedPrimaryKeyIdentifiers: String by lazy {
    primaryKeyFields.joinToString(", ") { it.name.lowerCamel }
  }

  val jdbcSerializedPrimaryKeyFields by lazy {
    commaSeparatedJavaFields(primaryKeyFields)
  }

  // For INSERT, PrimaryKey fields are first
  private fun buildInsertPreparedStatementSetterStatements(
    targetLanguage: TargetLanguage,
  ): String {

    val cfg = PreparedStatementBuilderConfig(
      allowFieldNonNullAssertion = true, //TODO fix this
      fieldOwner = RawExpression("entity"),
      fieldReadMode = targetLanguage.fieldReadMode,
      preparedStatementIdentifierExpression = RawExpression("ps")
    )

    val pk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = primaryKeyFields,
      firstIndex = 1,
    )

    val nonPk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = nonPrimaryKeyFields,
      firstIndex = JDBCColumnIndex(primaryKeyFields.size + 1),
    )

    return (pk + nonPk)
      .map { it.render(targetLanguage) }
      .joinToString(separator = "\n")
  }

  // NOTE: For UPDATE, PrimaryKey fields are last
  private fun buildUpdatePreparedStatementSetterStatements(
    targetLanguage: TargetLanguage,
  ): String {

    val cfg = PreparedStatementBuilderConfig(
      allowFieldNonNullAssertion = true, //TODO fix this
      fieldOwner = RawExpression("entity"),
      fieldReadMode = targetLanguage.fieldReadMode,
      preparedStatementIdentifierExpression = RawExpression("ps")
    )

    val nonPk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = nonPrimaryKeyFields,
      firstIndex = JDBCColumnIndex.FIRST,
    )

    val pk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = primaryKeyFields,
      firstIndex = JDBCColumnIndex(nonPrimaryKeyFields.size + 1),
    )

    val separator = RawExpression("\n\t\t// Primary key field(s)")

    return (nonPk + separator + pk)
      .map { it.render(targetLanguage) }
      .joinToString(separator = "\n")
  }

  private fun buildUpdateFieldPreparedStatementSetterStatements(
    field: Field,
    targetLanguage: TargetLanguage,
  ): String {
    val cfg = PreparedStatementBuilderConfig(
      allowFieldNonNullAssertion = false,
      fieldOwner = EmptyExpression, //TODO: verify
      fieldReadMode = FieldReadMode.DIRECT,
      preparedStatementIdentifierExpression = RawExpression("ps")
    )

    val columnSetterStatement = PreparedStatementSetExpression(
      columnIndex = JDBCColumnIndex.FIRST,
      field = field,
    )

    val pk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = primaryKeyFields,
      firstIndex = 2,
    )

    return (listOf(columnSetterStatement) + pk)
      .map { it.render(targetLanguage) }
      .joinToString(separator = "\n")
  }

  private fun buildPreparedStatementSetterStatementsForPrimaryKey(
    targetLanguage: TargetLanguage,
    fieldReadStyle: FieldReadMode = targetLanguage.fieldReadMode,
  ) =
    buildPreparedStatementSetters(
      cfg = PreparedStatementBuilderConfig(
        fieldReadStyle = fieldReadStyle,
        targetLanguage = targetLanguage,
      ),
      fields = primaryKeyFields,
      firstIndex = 1,
    )
      .map { it.render(targetLanguage) }
      .joinToString(separator = "\n")
}
