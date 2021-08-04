package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/** Represents javadoc, kdoc, godoc, rustdoc, jsdoc, tsdoc, cppdoc, ... */
data class Documentation(
  @JsonValue
  val value: String,
) {

  companion object {
    @JvmStatic
    @JsonCreator
    fun build(value: String) = Documentation(value)

    fun fromLinesAsJavadoc(lines: Collection<String>): Documentation {
      TODO()
    }

    val EMPTY = Documentation("")
  }

  fun isBlank(): Boolean = value.isBlank()

  fun isNotBlank(): Boolean = value.isNotBlank()
}
