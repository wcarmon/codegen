package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

/** [Field] attributes specific to relational database */
@JsonPropertyOrder(alphabetic = true)
data class RDBMSColumn(
  val autoIncrement: Boolean = false,
  val varcharLength: Int? = null,
  //TODO: represent foreign Keys
) {

  init {
    if (varcharLength != null) {
      require(varcharLength > 0) { "varcharLength must be positive" }
    }
  }
}
