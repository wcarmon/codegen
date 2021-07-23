package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonValue

/** Represents javadoc, jsdoc, tsdoc, cppdoc, ... */
data class Documentation(
  @JsonValue
  val value: String,
) {

  companion object {
    fun fromLinesAsJavadoc(lines: Collection<String>): Documentation {
      TODO()
    }

    val EMPTY = Documentation("")
  }
}
