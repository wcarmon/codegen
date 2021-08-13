package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.model.util.buildJavaPreconditionStatements
import com.wcarmon.codegen.model.util.getJavaImportsForFields
import com.wcarmon.codegen.model.util.javaMethodArgsForFields
import com.wcarmon.codegen.model.util.kotlinMethodArgsForFields
import com.wcarmon.codegen.model.utils.commaSeparatedColumnAssignment
import com.wcarmon.codegen.model.utils.commaSeparatedColumns
import com.wcarmon.codegen.model.utils.primaryKeyTableConstraint
import org.atteo.evo.inflector.English


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

  val rdbms: RDBMSTable? = null,

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
      .filter { it.rdbms != null }
      .filter { it.rdbms!!.positionInPrimaryKey != null }
      .map { it.rdbms!!.positionInPrimaryKey!! }

    require(pkPositions.size == pkPositions.toSet().size) {
      "All PK field positions must be distinct"
    }

    // -- Validate extraImplements
    require(extraImplements.size == extraImplements.toSet().size) {
      "remove duplicate extraImplements values: $extraImplements"
    }
  }

  val primaryKeyFields = fields
    .filter { it.rdbms != null }
    .filter { it.rdbms!!.positionInPrimaryKey != null }
    .sortedBy { it.rdbms!!.positionInPrimaryKey!! }

  val nonPrimaryKeyFields = fields
    .filter { it.rdbms == null || it.rdbms.positionInPrimaryKey == null }
    .sortedBy { it.name.lowerCamel }


  val collectionFields = fields
    .filter { it.type.base.isCollection }
    .sortedBy { it.name.lowerCamel }

  val commaSeparatedColumns = commaSeparatedColumns(this)

  val commentForPKFields =
    if (primaryKeyFields.isEmpty()) ""
    else "PK " + English.plural("field", primaryKeyFields.size)

  val dbSchemaPrefix =
    if (rdbms?.schema?.isBlank() != false) ""
    else "${rdbms.schema}."

  val fieldsWithValidation = fields
    .filter { it.validation != null }
    .sortedBy { it.name.lowerCamel }

  val hasCollectionFields =
    fields.any { it.type.base.isCollection }

  val hasNonPrimaryKeyFields = nonPrimaryKeyFields.isNotEmpty()

  val hasPrimaryKeyFields = primaryKeyFields.isNotEmpty()

  val javaImportsForFields: Set<String> = getJavaImportsForFields(this)

  val pkWhereClause = commaSeparatedColumnAssignment(primaryKeyFields)

  val primaryKeyTableConstraint = primaryKeyTableConstraint(this)

  val questionMarkStringForInsert = (1..fields.size).map { "?" }.joinToString()

  val sortedFields = fields.sortedBy { it.name.lowerCamel }

  val updateSetClause = commaSeparatedColumnAssignment(nonPrimaryKeyFields)

  fun javaMethodArgsForPKFields(qualified: Boolean) =
    javaMethodArgsForFields(primaryKeyFields, qualified)

  fun kotlinMethodArgsForPKFields(qualified: Boolean) =
    kotlinMethodArgsForFields(primaryKeyFields, qualified)

  val javaPrimaryKeyPreconditionStatements =
    buildJavaPreconditionStatements(primaryKeyFields)
      .joinToString("\n\t")
}
