package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.wcarmon.codegen.model.ast.Expression
import com.wcarmon.codegen.model.ast.RawStringExpression

/**
 * A template which can be formatted/expanded with [String.format]
 */
data class ExpressionTemplate(

  /** Must contain %s */
  private val value: String,
) {

  companion object {

    @JsonCreator
    @JvmStatic
    fun parse(v: String) = ExpressionTemplate(v)

    /** Direct serialization & deserialization (no wrapper/parser methods)*/
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
  fun expand(replacement: String): Expression =
    RawStringExpression(String.format(value, replacement))
}
