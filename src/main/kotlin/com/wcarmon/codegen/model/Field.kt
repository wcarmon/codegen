package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.CREATED_TS_FIELD_NAMES
import com.wcarmon.codegen.UPDATED_TS_FIELD_NAMES
import com.wcarmon.codegen.model.BaseFieldType.STRING
import com.wcarmon.codegen.model.TargetLanguage.JAVA_08
import com.wcarmon.codegen.model.TargetLanguage.KOTLIN_JVM_1_4
import com.wcarmon.codegen.view.JVMFieldView
import com.wcarmon.codegen.view.JavaFieldView
import com.wcarmon.codegen.view.KotlinFieldView
import com.wcarmon.codegen.view.RDBMSColumnView
import kotlin.text.RegexOption.IGNORE_CASE

/**
 * See src/main/resources/json-schema/field.schema.json
 *
 * Represents ...
 * - REST: resource property/attribute
 * - Protocol buffer: field
 * - RDBMS: column
 *
 * - C++: struct member, class data member
 * - C: struct member
 * - Golang: struct field
 * - Java: class field, record field
 * - Kotlin: class field
 * - Rust: struct field
 * - Typescript: property
 *
 * See extensions package for laguage & framework specific methods
 */
// `$id` and `$schema` are part of json standard, but not useful for code generation
@JsonIgnoreProperties("\u0024schema", "\u0024id")
@JsonPropertyOrder(alphabetic = true)
data class Field(
  val name: Name,

  val type: LogicalFieldType,

  /**
   * JSON                         | in here   | Interpretation
   * ---------------------------- | --------- | --------------
   * (no defaultValue attribute)  |
   * defaultValue: null           |
   * defaultValue: "null"         |
   * defaultValue: "'null'"       |
   * defaultValue: ""             |
   * defaultValue: "\"null\""     |
   */
  //TODO: add tests to enforce above
  val defaultValue: String? = null,

  val documentation: Documentation = Documentation.EMPTY,

  /**
   * null:  not a Id/PK field
   * 0:     1st part of Id/PK field
   * 1:     2nd part of Id/PK field
   * ...
   */
  val positionInId: Int? = null,

  // -- Technology specific config
  val jvmConfig: JVMFieldConfig = JVMFieldConfig(),
  val protobufConfig: ProtocolBufferFieldConfig = ProtocolBufferFieldConfig(),
  val rdbmsConfig: RDBMSColumnConfig = RDBMSColumnConfig(),
  val validationConfig: FieldValidation = FieldValidation(),
) {

  companion object {

    @JvmStatic
    @JsonCreator
    fun parse(
      @JsonProperty("defaultValue") defaultValue: String? = null,
      @JsonProperty("documentation") documentation: Documentation = Documentation.EMPTY,
      @JsonProperty("enumType") enumType: Boolean = false,
      @JsonProperty("jvm") jvmFieldConfig: JVMFieldConfig = JVMFieldConfig(),
      @JsonProperty("name") name: Name,
      @JsonProperty("nullable") nullable: Boolean = false,
      @JsonProperty("positionInId") positionInId: Int? = null,
      @JsonProperty("precision") precision: Int? = null,
      @JsonProperty("protobuf") protobufConfig: ProtocolBufferFieldConfig = ProtocolBufferFieldConfig(),
      @JsonProperty("rdbms") rdbmsConfig: RDBMSColumnConfig = RDBMSColumnConfig(),
      @JsonProperty("scale") scale: Int = 0,
      @JsonProperty("signed") signed: Boolean = true,
      @JsonProperty("type") typeLiteral: String = "",
      @JsonProperty("typeParameters") typeParameters: List<String> = listOf(),
      @JsonProperty("validation") validationConfig: FieldValidation = FieldValidation(),
    ): Field {

      //TODO: missing context
      require(typeLiteral.isNotBlank()) { "Field.type is required: this=$this" }

      //TODO: signed should override whatever is specified on type literal

      return Field(
        defaultValue = defaultValue,
        documentation = documentation,
        jvmConfig = jvmFieldConfig,
        name = name,
        positionInId = positionInId,
        protobufConfig = protobufConfig,
        rdbmsConfig = rdbmsConfig,
        type = LogicalFieldType(
          base = BaseFieldType.parse(typeLiteral),
          enumType = enumType,
          nullable = nullable,
          precision = precision,
          rawTypeLiteral = typeLiteral,
          scale = scale,
          signed = signed,
          typeParameters = typeParameters,
        ),
        validationConfig = validationConfig,
      )
    }
  }

  init {

    val isIdField = (positionInId ?: -1) >= 0
    if (isIdField) {
      require(!type.nullable) {
        "Id/PrimaryKey fields cannot be nullable: $this"
      }
    }

    //NOTE: precision and scale are validated on LogicalFieldType

    if (positionInId != null) {
      require(positionInId >= 0) {
        "positionInId must be non-negative: $positionInId, this=$this"
      }
    }
  }

  val java8View by lazy {
    JavaFieldView(this, jvmView, JAVA_08)
  }

  val kotlinView by lazy {
    KotlinFieldView(this, jvmView, KOTLIN_JVM_1_4)
  }

  val sqlView by lazy {
    RDBMSColumnView(this)
  }

  //TODO: improve me
  val hasDefault = defaultValue != null

  //TODO: improve me
  val shouldDefaultToNull: Boolean by lazy {
    //TODO: is this reusable & threadsafe?
    val regex =
      """^['"]?null['"]?$""".toRegex(IGNORE_CASE)

    defaultValue != null && regex.matches(defaultValue)
  }

  private val jvmView by lazy {
    JVMFieldView(this)
  }

  // -- Language & Framework specific convenience methods

  /**
   * Defaults to a reasonable RDBMS equivalent
   * Allows override via [RDBMSColumnConfig.overrideTypeLiteral]
   */
  //TODO: is this appropriate for JVM or only RDBMS?
  val effectiveBaseType by lazy {
    if (rdbmsConfig.overrideTypeLiteral.isNotBlank()) {
      BaseFieldType.parse(rdbmsConfig.overrideTypeLiteral)

    } else {
      type.base
    }
  }

  val isCollection: Boolean = effectiveBaseType.isCollection

  val shouldQuoteInString = when (effectiveBaseType) {
    STRING -> true
    else -> false
  }

  val usesNumericValidation = effectiveBaseType.isNumeric

  val usesStringValidation = effectiveBaseType == STRING


  val isCreatedTimestamp =
    effectiveBaseType.isTemporal &&
        CREATED_TS_FIELD_NAMES.any { name.lowerCamel.equals(it, true) }

  val isUpdatedTimestamp =
    effectiveBaseType.isTemporal &&
        UPDATED_TS_FIELD_NAMES.any { name.lowerCamel.equals(it, true) }
}
