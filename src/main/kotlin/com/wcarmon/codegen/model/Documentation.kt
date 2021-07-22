package com.wcarmon.codegen.model

/** Represents javadoc, jsdoc, tsdoc, cppdoc, ... */
data class Documentation(
  val value: String,
) {

  companion object {
    fun fromLinesAsJavadoc(lines: Collection<String>): Documentation {
      TODO()
    }

    val EMPTY = Documentation("")
  }
}
