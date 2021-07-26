package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

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


  // --- These are part of the type -----------
  /** Bounded set of acceptable values? */
  val enumType: Boolean = false,  // part of validation?

  val validation: FieldValidation = FieldValidation(),
) {

  companion object {

    @JvmStatic
    @JsonCreator
    fun parse(
      @JsonProperty("name") name: Name,
      @JsonProperty("defaultValue") defaultValue: String? = null,
      @JsonProperty("documentation") documentation: Documentation = Documentation.EMPTY,
      @JsonProperty("nullable") nullable: Boolean = false,
      @JsonProperty("precision") precision: Int = 0,
      @JsonProperty("rdbms") rdbms: RDBMSColumn? = null,
      @JsonProperty("scale") scale: Int = 0,
      @JsonProperty("signed") signed: Boolean = true,
      @JsonProperty("type") typeLiteral: String = "",
      @JsonProperty("typeParameters") typeParameters: List<String> = listOf(),
      @JsonProperty("validation") validation: FieldValidation = FieldValidation(),
      //TODO: enumType
    ): Field {

      //TODO: missing context
      require(typeLiteral.isNotBlank()) { "Field.type is required" }

      //TODO: signed should override whatever is specified on type literal

      return Field(
        defaultValue = defaultValue,
        documentation = documentation,
        //TODO: support enums
        enumType = false,
        name = name,
        rdbms = rdbms,
        type = LogicalFieldType(
          base = BaseFieldType.parse(typeLiteral),
          nullable = nullable,
          precision = precision,
          scale = scale,
          signed = signed,
          typeParameters = typeParameters,
        ),
        validation = validation,
      )
    }
  }
}
