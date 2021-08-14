package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.CREATED_TS_FIELD_NAMES
import com.wcarmon.codegen.UPDATED_TS_FIELD_NAMES
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.util.*
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

  val rdbms: RDBMSColumn? = null,

  val validation: FieldValidation? = null,
) {

  companion object {

    @JvmStatic
    @JsonCreator
    fun parse(
      @JsonProperty("defaultValue") defaultValue: String? = null,
      @JsonProperty("documentation") documentation: Documentation = Documentation.EMPTY,
      @JsonProperty("enumType") enumType: Boolean = false,
      @JsonProperty("jvmDeserializerTemplate") jvmDeserializerTemplate: String = "",
      @JsonProperty("jvmSerializerTemplate") jvmSerializerTemplate: String = "",
      @JsonProperty("name") name: Name,
      @JsonProperty("nullable") nullable: Boolean = false,
      @JsonProperty("precision") precision: Int? = null,
      @JsonProperty("rdbms") rdbms: RDBMSColumn? = null,
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
        name = name,
        rdbms = rdbms,
        type = LogicalFieldType(
          base = BaseFieldType.parse(typeLiteral),
          enumType = enumType,
          jvmDeserializerTemplate = jvmDeserializerTemplate,
          jvmSerializerTemplate = jvmSerializerTemplate,
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

    val isPrimaryKeyField = (rdbms?.positionInPrimaryKey ?: -1) >= 0
    if (isPrimaryKeyField) {
      require(!type.nullable) {
        "Primary key fields cannot be nullable: $this"
      }
    }

    //NOTE: precision and scale are validated on LogicalFieldType
  }

  val hasDefault = defaultValue != null

  val shouldDefaultToNull: Boolean by lazy {
    //TODO: is this reusable & threadsafe?
    val regex =
      """^['"]?null['"]?$""".toRegex(IGNORE_CASE)

    defaultValue != null && regex.matches(defaultValue)
  }

  // -- Language & Framework specific convenience methods (for velocity)
  val defaultValueLiteralForJVM by lazy {
    defaultValueLiteralForJVM(this)
  }

  val javaType = getJavaTypeLiteral(type, true)

  //TODO: test this on types that are already unqualified
  val unqualifiedJavaType = getJavaTypeLiteral(type, false)

  //TODO: test this on types that are already unqualified
  val unqualifiedKotlinType = getKotlinTypeLiteral(type, false)

  val kotlinType = getKotlinTypeLiteral(type)

  val isCollection: Boolean = type.base.isCollection

  val isPrimaryKeyField = (rdbms?.positionInPrimaryKey ?: -1) >= 0

  //TODO: move to jackson extensions file
  val jacksonTypeRef by lazy {
    require(type.isParameterized) {
      "type references are only required for parameterized types"
    }

    when (type.base) {
      LIST -> "List<${type.typeParameters[0]}>"
      SET -> "Set<${type.typeParameters[0]}>"
      else -> TODO("Build TypeReference for $this")
    }
  }

  val jdbcGetter = jdbcGetter(type)

  val jdbcSetter = jdbcSetter(type)

  val postgresqlColumnDefinition = postgresColumnDefinition(this)

  val shouldUseJVMDeserializer = shouldUseJVMDeserializer(type)

  val sqliteColumnDefinition = sqliteColumnDefinition(this)

  val shouldQuoteInString = when (type.base) {
    STRING -> true
    else -> false
  }


  //TODO: convert to fun,
  //    accept template placeholder replacement here
  //    rename
  val unmodifiableJavaCollectionMethod by lazy {
    unmodifiableJavaCollectionMethod(type.base)
  }

  val usesNumericValidation = type.base.isNumeric

  val usesStringValidation = type.base == STRING

  val resultSetGetterExpression by lazy {
    buildResultSetGetterExpression(this)
  }

  val isCreatedTimestamp =
    type.base.isTemporal &&
        CREATED_TS_FIELD_NAMES.any { name.lowerCamel.equals(it, true) }

  val isUpdatedTimestamp =
    type.base.isTemporal &&
        UPDATED_TS_FIELD_NAMES.any { name.lowerCamel.equals(it, true) }

  fun javaEqualityExpression(
    identifier0: String,
    identifier1: String,
  ) = com.wcarmon.codegen.model.util.javaEqualityExpression(
    type,
    name,
    identifier0,
    identifier1
  )

  fun jvmDeserializeTemplate(fieldValueExpression: String) =
    jvmDeserializeTemplate(type, fieldValueExpression)

  // Only invoke on collection types
  fun newJavaCollectionExpression() =
    newJavaCollectionExpression(type)
}
