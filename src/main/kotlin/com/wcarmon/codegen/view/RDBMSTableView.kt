package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.FieldValidationExpressions
import com.wcarmon.codegen.ast.InterFieldValidationExpression
import com.wcarmon.codegen.ast.RawLiteralExpression
import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.extensions.escapeDoubleQuotes
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.JDBCColumnIndex
import com.wcarmon.codegen.model.PreparedStatementBuilderConfig
import com.wcarmon.codegen.model.SQLPlaceholderType.NUMBERED_DOLLARS
import com.wcarmon.codegen.model.SQLPlaceholderType.QUESTION_MARK
import com.wcarmon.codegen.model.TargetLanguage.SQL_POSTGRESQL
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

  /**
   * SQLite should NOT use this (all others can)
   */
  val qualifiedTableName: String by lazy {
    "$schemaPrefix\"${entity.name.lowerSnake}\""
  }

  val qualifiedTableName_escaped: String by lazy {
    "$schemaPrefix\"${entity.name.lowerSnake}\"".escapeDoubleQuotes
  }


  val schemaPrefix: String =
    if (entity.rdbmsConfig.schema.isBlank()) ""
    else "\"${entity.rdbmsConfig.schema}\"."


  val primaryKeyWhereClause_questionMarks: String = commaSeparatedColumnAssignment(
    fields = entity.idFields,
    placeholderType = QUESTION_MARK,
  )

  fun primaryKeyWhereClause_numberedDollars(
    firstIndex: Int = 1,
  ): String {
    require(firstIndex >= 1)

    return commaSeparatedColumnAssignment(
      fields = entity.idFields,
      firstIndex = firstIndex,
      placeholderType = NUMBERED_DOLLARS,
    )
  }

  val constraints: String by lazy {

    val parts = mutableListOf<String>()

    if (entity.hasIdFields) {
      parts += entity.rdbmsView.primaryKeyTableConstraint
    }

    val unique = uniqueConstraints
    if (unique.isNotBlank()) {
      parts += unique
    }

    val check = checkConstraints
    if (check.isNotBlank()) {
      parts += check
    }

    val interField = interFieldCheckConstraints
    if (interField.isNotBlank()) {
      parts += interField
    }

    val output = parts.joinToString(separator = ",\n\n")

    if (output.isNotBlank()) {
      ",\n$output"
    } else {
      output
    }
  }

  val uniqueConstraints: String by lazy {
    //TODO: more here
    ""
  }

  val interFieldCheckConstraints: String by lazy {
    val renderConfig = RenderConfig(
      debugMode = debugMode,
      targetLanguage = SQL_POSTGRESQL,
      terminate = false,
    )

    entity
      .interFieldValidations
      .joinToString(
        separator = ",\n"
      ) { v ->
        InterFieldValidationExpression(
          entity = entity,
          validationConfig = v,
          validationSeparator = ",\n"
        )
          .render(renderConfig)
      }
  }

  val checkConstraints: String by lazy {
    val targetLanguage = SQL_POSTGRESQL

    val renderConfig = RenderConfig(
      debugMode = debugMode,
      targetLanguage = targetLanguage,
      terminate = false,
    )

    validatedFields.map { field ->
      FieldValidationExpressions(
        field = field,
        tableConstraintPrefix = "${entity.name.lowerSnake}_",
        validationConfig = field.effectiveFieldValidation(targetLanguage),
        validationSeparator = ",\n",
      )
        .render(renderConfig)
    }
      .filter { it.isNotBlank() }
      .joinToString(
        separator = ",\n",
      )
  }

  val primaryKeyTableConstraint: String = primaryKeyTableConstraint(entity)

  val questionMarkStringForInsert: String = (1..entity.fields.size).joinToString { "?" }

  fun numberedDollarStringForInsert(
    firstIndex: Int = 1,
  ): String {
    require(firstIndex >= 1) { "numbered dollar indexes start at 1" }

    return (1..entity.fields.size)
      .mapIndexed { index, field ->
        "\u0024${index + firstIndex}"
      }
      .joinToString(separator = ", ")
  }

  val updateSetClause_questionMarks: String = commaSeparatedColumnAssignment(
    fields = entity.nonIdFields,
    placeholderType = QUESTION_MARK,
  )

  private val validatedFields =
    entity.sortedFieldsWithIdsFirst
      .filter {
        it.effectiveFieldValidation(SQL_POSTGRESQL).hasValidation
      }

  val updateSetClause_numeredDollars: String = commaSeparatedColumnAssignment(
    fields = entity.nonIdFields,
    placeholderType = NUMBERED_DOLLARS,
  )

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
