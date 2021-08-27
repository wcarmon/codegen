package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.CREATED_TS_FIELD_NAMES
import com.wcarmon.codegen.UPDATED_TS_FIELD_NAMES
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.TargetLanguage.JAVA_08
import com.wcarmon.codegen.model.TargetLanguage.KOTLIN_JVM_1_4
import com.wcarmon.codegen.model.util.defaultValueLiteralForJVM
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

  val rdbms: RDBMSColumnConfig = RDBMSColumnConfig(),

  val jvm: JVMFieldConfig = JVMFieldConfig(),

  val protobuf: ProtocolBufferFieldConfig = ProtocolBufferFieldConfig(),

  val validation: FieldValidation? = null,
) {

  companion object {

    //TODO: simplify so I can use jackson structure directly
    @JvmStatic
    @JsonCreator
    fun parse(
      @JsonProperty("defaultValue") defaultValue: String? = null,
      @JsonProperty("documentation") documentation: Documentation = Documentation.EMPTY,
      @JsonProperty("enumType") enumType: Boolean = false,
      @JsonProperty("jvm") jvmFieldConfig: JVMFieldConfig? = null,
      @JsonProperty("name") name: Name,
      @JsonProperty("nullable") nullable: Boolean = false,
      @JsonProperty("precision") precision: Int? = null,
      @JsonProperty("protobuf") protobuf: ProtocolBufferFieldConfig? = null,
      @JsonProperty("rdbms") rdbms: RDBMSColumnConfig? = null,
      @JsonProperty("scale") scale: Int = 0,
      @JsonProperty("signed") signed: Boolean = true,
      @JsonProperty("type") typeLiteral: String = "",
      @JsonProperty("typeParameters") typeParameters: List<String> = listOf(),
      @JsonProperty("validation") validation: FieldValidation? = null,
    ): Field {

      //TODO: missing context
      require(typeLiteral.isNotBlank()) { "Field.type is required: this=$this" }

      //TODO: signed should override whatever is specified on type literal

      return Field(
        defaultValue = defaultValue,
        documentation = documentation,
        jvm = jvmFieldConfig ?: JVMFieldConfig(),
        name = name,
        protobuf = protobuf ?: ProtocolBufferFieldConfig(),
        rdbms = rdbms ?: RDBMSColumnConfig(),
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
        validation = validation,
      )
    }
  }

  init {

    val isPrimaryKeyField = (rdbms.positionInPrimaryKey ?: -1) >= 0
    if (isPrimaryKeyField) {
      require(!type.nullable) {
        "Primary key fields cannot be nullable: $this"
      }
    }

    //NOTE: precision and scale are validated on LogicalFieldType
  }

  val java8View by lazy {
    JavaFieldView(this, JAVA_08)
  }

  val kotlinView by lazy {
    KotlinFieldView(this, KOTLIN_JVM_1_4)
  }

  val sqlView by lazy {
    RDBMSColumnView(this)
  }

  val hasDefault = defaultValue != null

  val shouldDefaultToNull: Boolean by lazy {
    //TODO: is this reusable & threadsafe?
    val regex =
      """^['"]?null['"]?$""".toRegex(IGNORE_CASE)

    defaultValue != null && regex.matches(defaultValue)
  }

  // -- Language & Framework specific convenience methods
  val defaultValueLiteralForJVM by lazy {
    defaultValueLiteralForJVM(this)
  }

  /**
   * Defaults to a reasonable RDBMS equivalent
   * Allows override via [RDBMSColumnConfig.overrideTypeLiteral]
   */
  //TODO: is this appropriate for JVM too?
  val effectiveBaseType by lazy {
    if (rdbms.overrideTypeLiteral.isNotBlank()) {
      BaseFieldType.parse(rdbms.overrideTypeLiteral)

    } else {
      type.base
    }
  }

  val isCollection: Boolean = effectiveBaseType.isCollection

  //TODO: move to jackson extensions file
  val jacksonTypeRef by lazy {
    require(type.isParameterized) {
      "type references are only required for parameterized types"
    }

    when (effectiveBaseType) {
      LIST -> "List<${type.typeParameters[0]}>"
      SET -> "Set<${type.typeParameters[0]}>"
      else -> TODO("Build TypeReference for $this")
    }
  }

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
