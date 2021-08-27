package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.ast.FieldReadMode
import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.ast.RawExpression
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.model.util.*
import com.wcarmon.codegen.view.JavaEntityView
import com.wcarmon.codegen.view.KotlinEntityView
import com.wcarmon.codegen.view.RDBMSTableView


/**
 * See src/main/resources/json-schema/entity.schema.json
 *
 * Represents ...
 * - REST: Resource
 * - Protocol buffer: Message
 * - RDBMS: Table
 *
 * - Kotlin: data class, POJO class
 * - Java: Record, POJO class
 * - Golang: struct
 * - Rust: struct
 * - c: struct
 * - c++: class (heap) or struct (stack)
 * - Typescript: interface or class
 */
@JsonIgnoreProperties("\u0024schema", "\u0024id")
@JsonPropertyOrder(alphabetic = true)
data class Entity(
  val name: Name,
  val pkg: PackageName,
  val documentation: Documentation = Documentation.EMPTY,

  val canCheckForExistence: Boolean = true,
  val canCreate: Boolean = true,
  val canDelete: Boolean = true,
  val canExtend: Boolean = false,
  val canFindByPK: Boolean = true,
  val canList: Boolean = true,
  val canUpdate: Boolean = true,

  // Likely easier to specify directly in template
  // unique, order matters
  val extraImplements: List<String> = listOf(),

  val fields: List<Field>,

  val rdbms: RDBMSTableConfig = RDBMSTableConfig(),

  // TODO: list: pagination
  // TODO: list: order by fieldX, asc|desc
) {

  init {
    require(fields.isNotEmpty()) { "At least one field required" }

    // -- Validate field names are unique
    val fieldNames = fields.map { it.name }
    require(fieldNames.size == fieldNames.toSet().size) {
      "field names must be unique: entity=${name.lowerCamel}, fieldNames=$fieldNames"
    }

    // -- Validate PK fields
    val pkPositions = fields
      .filter { it.rdbms.positionInPrimaryKey != null }
      .map { it.rdbms.positionInPrimaryKey!! }

    require(pkPositions.size == pkPositions.toSet().size) {
      "All PK field positions must be distinct"
    }

    // -- Validate extraImplements
    require(extraImplements.size == extraImplements.toSet().size) {
      "remove duplicate extraImplements values: $extraImplements"
    }
  }

  val java8View by lazy {
    JavaEntityView(this, JAVA_08)
  }

  val kotlinView by lazy {
    KotlinEntityView(this, KOTLIN_JVM_1_4)
  }

  val sqlView by lazy {
    RDBMSTableView(this)
  }

  val collectionFields = fields
    .filter { it.effectiveBaseType.isCollection }
    .sortedBy { it.name.lowerCamel }

  val fieldsWithValidation = fields
    .filter { it.validation != null }
    .sortedBy { it.name.lowerCamel }

  val requiresObjectWriter =
    fields.any { it.effectiveBaseType.isCollection }

  val requiresObjectReader =
    fields.any { it.effectiveBaseType.isCollection }

  val sortedFields = fields.sortedBy { it.name.lowerCamel }

  val sortedFieldsWithPKFirst =
    primaryKeyFields + nonPrimaryKeyFields

  //TODO: make an SQLExpression?
  val updateSetClause = commaSeparatedColumnAssignment(nonPrimaryKeyFields)


  val commaSeparatedPrimaryKeyIdentifiers by lazy {
    primaryKeyFields.joinToString(", ") { it.name.lowerCamel }
  }

  val jdbcSerializedPKFields by lazy {
    commaSeparatedJavaFields(primaryKeyFields)
  }

  val protocolBufferFields by lazy {
    buildProtoBufMessageFieldDeclarations(
      primaryKeyFields,
      nonPrimaryKeyFields
    )
      .map { "  " + it.serialize(PROTOCOL_BUFFERS_3) }
      .joinToString("\n")
  }

  // For INSERT, PK fields are first
  private fun buildInsertPreparedStatementSetterStatements(
    targetLanguage: TargetLanguage,
  ): String {

    val cfg = PreparedStatementBuilderConfig(
      fieldReadPrefix = "entity.",
      targetLanguage = targetLanguage,
    )

    val pk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = primaryKeyFields,
      firstIndex = 1,
    )

    val nonPk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = nonPrimaryKeyFields,
      firstIndex = primaryKeyFields.size + 1,
    )

    return (pk + nonPk)
      .map { it.serialize(targetLanguage) }
      .joinToString(separator = "\n")
  }

  // NOTE: For UPDATE, PK fields are last
  private fun buildUpdatePreparedStatementSetterStatements(
    targetLanguage: TargetLanguage,
  ): String {

    val cfg = PreparedStatementBuilderConfig(
      fieldReadPrefix = "entity.",
      targetLanguage = targetLanguage,
      preparedStatementIdentifier = "ps",
    )

    val nonPk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = nonPrimaryKeyFields,
      firstIndex = 1,
    )

    val pk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = primaryKeyFields,
      firstIndex = nonPrimaryKeyFields.size + 1,
    )

    val separator = RawExpression("\n\t\t// Primary key field(s)")

    return (nonPk + separator + pk)
      .map { it.serialize(targetLanguage) }
      .joinToString(separator = "\n")
  }

  private fun buildUpdateFieldPreparedStatementSetterStatements(
    field: Field,
    targetLanguage: TargetLanguage,
  ): String {
    val cfg = PreparedStatementBuilderConfig(
      allowFieldNonNullAssertion = false,
      fieldReadStyle = DIRECT,
      targetLanguage = targetLanguage,
    )

    val columnSetterStatement = buildPreparedStatementSetter(
      cfg = cfg,
      columnIndex = 1,
      field = field,
    )

    val pk = buildPreparedStatementSetters(
      cfg = cfg,
      fields = primaryKeyFields,
      firstIndex = 2,
    )

    return (listOf(columnSetterStatement) + pk)
      .map { it.serialize(targetLanguage) }
      .joinToString(separator = "\n")
  }

  private fun buildPreparedStatementSetterStatementsForPK(
    targetLanguage: TargetLanguage,
    fieldReadStyle: FieldReadMode = targetLanguage.fieldReadStyle,
  ) =
    buildPreparedStatementSetters(
      cfg = PreparedStatementBuilderConfig(
        fieldReadStyle = fieldReadStyle,
        targetLanguage = targetLanguage,
      ),
      fields = primaryKeyFields,
      firstIndex = 1,
    )
      .map { it.serialize(targetLanguage) }
      .joinToString(separator = "\n")
}
