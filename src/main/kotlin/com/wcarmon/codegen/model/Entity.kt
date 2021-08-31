package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.DEBUG_MODE
import com.wcarmon.codegen.model.TargetLanguage.JAVA_08
import com.wcarmon.codegen.model.TargetLanguage.KOTLIN_JVM_1_4
import com.wcarmon.codegen.view.JVMEntityView
import com.wcarmon.codegen.view.Java8EntityView
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

  /** No leading comment markers (no leading slashes, no leading asterisk) */
  val documentation: List<String> = listOf(),

  val canCheckForExistence: Boolean = true,
  val canCreate: Boolean = true,
  val canDelete: Boolean = true,
  val canExtend: Boolean = false,
  val canFindById: Boolean = true,
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

    // -- Validate Id/PrimaryKey fields
    val idPositions = fields
      .filter { it.positionInId != null }
      .map { it.positionInId!! }

    require(idPositions.size == idPositions.toSet().size) {
      "All Id/PrimaryKey field positions must be unique"
    }

    // -- Validate extraImplements
    require(extraImplements.size == extraImplements.toSet().size) {
      "remove duplicate extraImplements values: $extraImplements"
    }
  }

  val java8View by lazy {
    Java8EntityView(
      debugMode = DEBUG_MODE,
      entity = this,
      jvmView = jvmView,
      rdbmsView = rdbmsView,
      targetLanguage = JAVA_08,
    )
  }

  val kotlinView by lazy {
    KotlinEntityView(
      debugMode = DEBUG_MODE,
      entity = this,
      jvmView = jvmView,
      rdbmsView = rdbmsView,
      targetLanguage = KOTLIN_JVM_1_4,
    )
  }

  val jvmView by lazy {
    JVMEntityView(
      debugMode = DEBUG_MODE,
      entity = this,
    )
  }

  val rdbmsView: RDBMSTableView by lazy {
    RDBMSTableView(
      debugMode = DEBUG_MODE,
      entity = this,
    )
  }

  val sqlView by lazy {
    RDBMSTableView(
      debugMode = DEBUG_MODE,
      entity = this,
    )
  }

  val idFields = fields
    .filter { it.positionInId != null }
    .sortedBy { it.positionInId!! }

  val nonIdFields = fields
    .filter { it.positionInId == null }
    .sortedBy { it.name.lowerCamel }

  val hasIdFields: Boolean = idFields.isNotEmpty()

  val hasNonIdFields: Boolean = nonIdFields.isNotEmpty()

  val collectionFields = fields
    .filter { it.effectiveBaseType.isCollection }
    .sortedBy { it.name.lowerCamel }

  val sortedFields = fields.sortedBy { it.name.lowerCamel }

  val sortedFieldsWithIdsFirst =
    idFields + nonIdFields

  val validatedFields = fields
    .filter { it.validationConfig.hasValidation }
    .sortedBy { it.name.lowerCamel }
}
