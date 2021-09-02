package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RawLiteralExpression
import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.JDBCColumnIndex
import com.wcarmon.codegen.model.PreparedStatementBuilderConfig
import com.wcarmon.codegen.util.*
import org.atteo.evo.inflector.English

/**
 * RDBMS related convenience methods for a [Entity]
 * See [com.wcarmon.codegen.model.RDBMSTableConfig]
 */
data class RDBMSTableView(
  private val debugMode: Boolean,
  private val entity: Entity,
) {

  val commaSeparatedColumns: String = commaSeparatedColumns(entity)

  //TODO: return Documentation
  val commentForPrimaryKeyFields: String =
    if (entity.idFields.isEmpty()) ""
    else "PrimaryKey " + English.plural("field", entity.idFields.size)

  val schemaPrefix: String =
    if (entity.rdbmsConfig.schema.isBlank()) ""
    else "${entity.rdbmsConfig.schema}."


  val primaryKeyWhereClause: String = commaSeparatedColumnAssignment(entity.idFields)

  val primaryKeyTableConstraint: String = primaryKeyTableConstraint(entity)

  val questionMarkStringForInsert: String = (1..entity.fields.size).joinToString { "?" }

  val updateSetClause: String = commaSeparatedColumnAssignment(entity.nonIdFields)

  val commaSeparatedPrimaryKeyIdentifiers: String by lazy {
    entity.idFields.joinToString(", ") { it.name.lowerCamel }
  }

  val jdbcSerializedPrimaryKeyFields by lazy {
    commaSeparatedJavaFields(entity.idFields)
  }

  // For INSERT, PrimaryKey fields are first
  fun insertPreparedStatementSetterStatements(
    renderConfig: RenderConfig,
  ): String {

    val psConfig = PreparedStatementBuilderConfig(
      allowFieldNonNullAssertion = true, //TODO fix this
      fieldOwner = RawLiteralExpression("entity"),
      fieldReadMode = renderConfig.targetLanguage.fieldReadMode,
      preparedStatementIdentifierExpression = RawLiteralExpression("ps")
    )

    val pk = buildPreparedStatementSetters(
      psConfig = psConfig,
      fields = entity.idFields,
      firstIndex = JDBCColumnIndex.FIRST,
    )

    val nonPk = buildPreparedStatementSetters(
      psConfig = psConfig,
      fields = entity.nonIdFields,
      firstIndex = JDBCColumnIndex(entity.idFields.size + 1),
    )

    return (pk + nonPk)
      .joinToString(separator = "\n") {
        it.render(renderConfig)
      }
  }

  // NOTE: For UPDATE, PrimaryKey fields are last
  fun updatePreparedStatementSetterStatements(
    renderConfig: RenderConfig,
  ): String {

    val psConfig = PreparedStatementBuilderConfig(
      allowFieldNonNullAssertion = true, //TODO fix this
      fieldOwner = RawLiteralExpression("entity"),
      fieldReadMode = renderConfig.targetLanguage.fieldReadMode,
      preparedStatementIdentifierExpression = RawLiteralExpression("ps")
    )

    val nonPk = buildPreparedStatementSetters(
      psConfig = psConfig,
      fields = entity.nonIdFields,
      firstIndex = JDBCColumnIndex.FIRST,
    )

    val pk = buildPreparedStatementSetters(
      psConfig = psConfig,
      fields = entity.idFields,
      firstIndex = JDBCColumnIndex(entity.nonIdFields.size + 1),
    )

    val separator = RawLiteralExpression("\n\t\t// Primary key field(s)")

    return (nonPk + separator + pk)
      .joinToString(separator = "\n") {
        it.render(renderConfig)
      }
  }
}
