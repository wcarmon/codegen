package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

private const val MAX_TYPE_LITERAL_LENGTH = 64

/**
 * [Field] attributes specific to relational database column
 *
 * See src/main/resources/json-schema/rdbms-column.schema.json
 */
@JsonPropertyOrder(alphabetic = true)
data class RDBMSColumnConfig(
  val autoIncrement: Boolean = false,

  val varcharLength: Int? = null,

  val overrideSerde: Serde = Serde.INLINE,

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

  val overridesType = overrideTypeLiteral.isNotBlank()
}
