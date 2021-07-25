package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * [Field] attributes specific to relational database column
 *
 * See src/main/resources/json-schema/rdbms-column.schema.json
 */
@JsonPropertyOrder(alphabetic = true)
data class RDBMSColumn(
  val autoIncrement: Boolean = false,

  /**
   * null: not a PK field
   * 0: 1st part of PK field
   * 1: 2nd part of PK field
   * ...
   */
  val positionInPrimaryKey: Int? = null,

  val varcharLength: Int? = null,
  //TODO: represent foreign Keys
) {

  init {
    if (varcharLength != null) {
      require(varcharLength > 0) { "varcharLength must be positive" }
    }

    if (positionInPrimaryKey != null) {
      require(positionInPrimaryKey >= 0) { "positionInPrimaryKey must be non-negative: $positionInPrimaryKey" }
    }
  }
}
