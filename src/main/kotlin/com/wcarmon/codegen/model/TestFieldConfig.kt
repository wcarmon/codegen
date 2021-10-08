package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic = true)
data class TestFieldConfig(
  val randomValueBuilder: String = "",
) {

  init {
    require(randomValueBuilder.trim() == randomValueBuilder) {
      "randomValueBuilder must be trimmed: $randomValueBuilder"
    }
  }
}
