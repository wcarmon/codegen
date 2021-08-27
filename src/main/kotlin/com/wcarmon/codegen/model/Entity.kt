package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.model.TargetLanguage.JAVA_08
import com.wcarmon.codegen.model.TargetLanguage.KOTLIN_JVM_1_4
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

  val rdbmsConfig: RDBMSTableConfig = RDBMSTableConfig(),

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
      .filter { it.rdbmsConfig.positionInPrimaryKey != null }
      .map { it.rdbmsConfig.positionInPrimaryKey!! }

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
    .sortedBy { it.name.lowerCamel }

  val requiresObjectWriter =
    fields.any { it.effectiveBaseType.isCollection }

  val requiresObjectReader =
    fields.any { it.effectiveBaseType.isCollection }

  val sortedFields = fields.sortedBy { it.name.lowerCamel }
}
