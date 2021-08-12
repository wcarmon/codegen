package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

private const val MAX_TYPE_LITERAL_LENGTH = 64

/**
 * [Field] attributes specific to relational database column
 *
 * See src/main/resources/json-schema/rdbms-column.schema.json
 */
@JsonPropertyOrder(alphabetic = true)
data class RDBMSColumn(
  val autoIncrement: Boolean = false,

  /**
   * null:  not a PK field
   * 0:     1st part of PK field
   * 1:     2nd part of PK field
   * ...
   */
  val positionInPrimaryKey: Int? = null,

  val varcharLength: Int? = null,

  /**
   * fully qualified static function/method
   * Use %s as a placeholder for the serialized string
   *
   * eg. "com.foo.MyType.fromDBString(%s)"
   *
   * No statement terminator required (no trailing semicolon)
   */
  val deserializerTemplate: String = "",

  /**
   * instance method or static method/function
   * Use %s as a placeholder for the field
   *
   * eg. "%s.toDBString()"
   *
   * No statement terminator required (no trailing semicolon)
   */
  val serializerTemplate: String = "",

  /**
   * Replace the auto-derived type with this literal
   */
  val overrideTypeLiteral: String = "",

  /**
   * For VARCHAR columns, wrap in single quotes
   */
  val overrideDefaultValue: String? = null,

  //TODO: represent foreign Keys
) {

  init {
    if (varcharLength != null) {
      require(varcharLength > 0) { "varcharLength must be positive: this=$this" }
    }

    if (positionInPrimaryKey != null) {
      require(positionInPrimaryKey >= 0) {
        "positionInPrimaryKey must be non-negative: $positionInPrimaryKey, this=$this"
      }
    }

    // -- Serde
    if (deserializerTemplate.isNotBlank()) {
      require(serializerTemplate.isNotBlank()) {
        "(rdbms) serializerTemplate required (to match deserializerTemplate): this=$this"
      }

      require(deserializerTemplate.contains("%s")) {
        "(rdbms) deserializerTemplate must contain a placeholder for the serialized string: this=$this"
      }
    }

    if (serializerTemplate.isNotBlank()) {
      require(deserializerTemplate.isNotBlank()) {
        "(rdbms) deserializerTemplate required (to match serializerTemplate): this=$this"
      }

      require(serializerTemplate.contains("%s")) {
        "(rdbms) serializerTemplate must contain a placeholder for the field: this=$this"
      }
    }

    require(serializerTemplate.trim() == serializerTemplate) {
      "(rdbms) serializerTemplate must be trimmed: this=$this"
    }

    require(deserializerTemplate.trim() == deserializerTemplate) {
      "(rdbms) deserializerTemplate must be trimmed: this=$this"
    }

    require(overrideTypeLiteral.length < MAX_TYPE_LITERAL_LENGTH) {
      "overrideTypeLiteral too long: $overrideTypeLiteral, this=$this"
    }

    // -- Incompatible pairs
    if (overrideTypeLiteral.isNotBlank()) {
      require(varcharLength == null) {
        "Cannot set varchar length when overriding type: this=$this"
      }
    }

    //TODO: warn in situations where varcharLength will be ignored
  }

  val hasCustomSerde = deserializerTemplate.isNotBlank() || serializerTemplate.isNotBlank()

  val overridesType = overrideTypeLiteral.isNotBlank()
}
