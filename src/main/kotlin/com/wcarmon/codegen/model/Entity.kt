package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.DEBUG_MODE
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.view.*


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

  val canCheckForExistence: Boolean = true,
  val canCreate: Boolean = true,
  val canDelete: Boolean = true,
  val canExtend: Boolean = false,
  val canFindById: Boolean = true,
  val canList: Boolean = true,
  val canUpdate: Boolean = true,

  /**
   * Automatically updated when created
   */
  val createdTimestampFieldName: Name? = null,

  /** No leading comment markers (no leading slashes, no leading asterisk) */
  val documentation: List<String> = listOf(),

  // Likely easier to specify directly in template
  // unique, order matters
  val extraImplements: List<String> = listOf(),

  val fields: List<Field>,

  /**
   * ID fields are implicitly indexed
   */
  val indexes: Set<Index> = setOf(),

  val rdbmsConfig: RDBMSTableConfig = RDBMSTableConfig(),

  /**
   * Automatically updated when modified/updated
   */
  val updatedTimestampFieldName: Name? = null,

  // TODO: list: pagination
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

    // -- Validate indexes
    indexes
      .flatMap { it.fieldNames }
      .distinct()
      .forEach { indexFieldName ->
        require(fields.any { it.name == indexFieldName }) {
          "Cannot find property on ${name.upperCamel} matching the Index field: ${indexFieldName.lowerCamel}"
        }
      }

    val idFields = fields
      .filter { it.positionInId != null }

    indexes
      .filter { it.size == 1 }
      .forEach { index ->
        require(idFields.none { it.name == index.first }) {
          "Remove unnecessary index for ID field: ${name.upperCamel}.${index.first.lowerCamel}"
        }
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

  val protobufView by lazy {
    ProtobufEntityView(
      debugMode = DEBUG_MODE,
      entity = this,
      targetLanguage = PROTOCOL_BUFFERS_3,
    )
  }

  val sqlView by lazy {
    RDBMSTableView(
      debugMode = DEBUG_MODE,
      entity = this,
    )
  }

  val sqlDelightView by lazy {
    SQLDelightTableView(
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

  val patchableFields = nonIdFields
    .filter { it.canUpdate }

  val hasIdFields: Boolean = idFields.isNotEmpty()

  val hasNonIdFields: Boolean = nonIdFields.isNotEmpty()

  val collectionFields = fields
    .filter { it.effectiveBaseType.isCollection }
    .sortedBy { it.name.lowerCamel }

  val sortedFields = fields.sortedBy { it.name.lowerCamel }

  val sortedFieldsWithIdsFirst =
    idFields + nonIdFields

  val validatedFields =
    sortedFieldsWithIdsFirst
      .filter {
        it.validationConfig.hasValidation
            // For Java we need validation to enforce null safety
            || !it.type.nullable
      }
}
