package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.model.BaseFieldType.*

/**
 * See src/main/resources/json-schema/field.schema.json
 *
 * Represents ...
 * - REST: resource property/attribute
 * - Protocol buffer: field
 * - RDBMS: column
 *
 * - Kotlin: class field
 * - Java: class field, record field
 * - Golang: struct field
 * - Rust: struct field
 * - c: struct member
 * - c++: struct member, class data member
 * - Typescript: property
 */
@JsonIgnoreProperties("\u0024schema", "\u0024id")
@JsonPropertyOrder(alphabetic = true)
data class Field(
  val name: Name,

  val type: LogicalFieldType,

  val defaultValue: String? = null,

  val documentation: Documentation = Documentation.EMPTY,

  val rdbms: RDBMSColumn? = null,

  val validation: FieldValidation = FieldValidation(),
) {

  companion object {

    @JvmStatic
    @JsonCreator
    fun parse(
      @JsonProperty("name") name: Name,
      @JsonProperty("defaultValue") defaultValue: String? = null,
      @JsonProperty("documentation") documentation: Documentation = Documentation.EMPTY,
      @JsonProperty("enumType") enumType: Boolean = false,
      @JsonProperty("nullable") nullable: Boolean = false,
      @JsonProperty("precision") precision: Int = 0,
      @JsonProperty("rdbms") rdbms: RDBMSColumn? = null,
      @JsonProperty("scale") scale: Int = 0,
      @JsonProperty("signed") signed: Boolean = true,
      @JsonProperty("type") typeLiteral: String = "",
      @JsonProperty("typeParameters") typeParameters: List<String> = listOf(),
      @JsonProperty("validation") validation: FieldValidation = FieldValidation(),
    ): Field {

      //TODO: missing context
      require(typeLiteral.isNotBlank()) { "Field.type is required" }

      //TODO: signed should override whatever is specified on type literal

      return Field(
        defaultValue = defaultValue,
        documentation = documentation,
        name = name,
        rdbms = rdbms,
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

  fun isPrimaryKeyField(): Boolean {
    return rdbms?.positionInPrimaryKey ?: -1 >= 0
  }

  fun javaEqualityExpression(identifier0: String, identifier1: String): String {
    require(identifier0.isNotBlank())
    require(identifier1.isNotBlank())

    if (type.enumType || type.base == BOOLEAN || type.base == CHAR) {
      return "$identifier0.${name.lowerCamel} == $identifier1.${name.lowerCamel}"
    }

    if (type.base == FLOAT_64) {
      return "Double.compare($identifier0.${name.lowerCamel}, $identifier1.${name.lowerCamel}) == 0"
    }

    if (type.base == FLOAT_32) {
      return "Float.compare($identifier0.${name.lowerCamel}, $identifier1.${name.lowerCamel}) == 0"
    }

    if (type.base == ARRAY) {
      return "Arrays.deepEquals($identifier0.${name.lowerCamel}, $identifier1.${name.lowerCamel})"
    }

    return "Objects.equals($identifier0.${name.lowerCamel}, $identifier1.${name.lowerCamel})"
  }

  fun shouldQuoteInString() = when (type.base) {
    STRING -> true
    else -> false
  }

  fun jacksonTypeRef(): String {
    require(type.base.isParameterized()) {
      "type references are only required for parameterized types"
    }

    return when (type.base) {
      LIST -> "List<${type.typeParameters[0]}>"
      SET -> "Set<${type.typeParameters[0]}>"
      else -> TODO("Build TypeReference for $this")
    }
  }
}
