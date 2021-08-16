package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * A template which can be formatted/expanded with [String.format]
 */
data class ExpressionTemplate(
  private val value: String,
) {

  companion object {

    @JsonCreator
    @JvmStatic
    fun parse(v: String) = ExpressionTemplate(v)

    @JvmStatic
    val INLINE = ExpressionTemplate("%s")
  }

  init {
    require(value.isNotBlank()) {
      "Template expression cannot be blank"
    }

    require(value.contains("%s")) {
      "Template expressions must contain a placeholder '%s': value=$value"
    }

    require(value.trim() == value) {
      "Template expression must be trimmed: value=$value"
    }
  }

  //TODO: document me
  fun expand(replacement: String) =
    String.format(value, replacement)
}
