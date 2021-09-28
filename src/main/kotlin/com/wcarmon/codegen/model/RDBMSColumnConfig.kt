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

  /**
   * Replace the auto-derived type with this literal
   */
  val overrideTypeLiteral: String? = null,

  /**
   * For VARCHAR columns, wrap in single quotes
   */
  val defaultValue: DefaultValue = DefaultValue(),

  //TODO: represent foreign Keys
) {

  init {
    if (varcharLength != null) {
      require(varcharLength > 0) { "varcharLength must be positive: this=$this" }
    }


    if (overrideTypeLiteral != null) {
      require(overrideTypeLiteral.isNotBlank()) {
        "overrideTypeLiteral must be null or non-blank"
      }

      require(overrideTypeLiteral.length < MAX_TYPE_LITERAL_LENGTH) {
        "overrideTypeLiteral too long: $overrideTypeLiteral, this=$this"
      }

      require(varcharLength == null) {
        "Cannot set varchar length when overriding type: this=$this"
      }
    }

    //TODO: warn in situations where varcharLength will be ignored
  }

  val overrideBaseType: BaseFieldType? =
    overrideTypeLiteral?.let { BaseFieldType.parse(it) }
}
