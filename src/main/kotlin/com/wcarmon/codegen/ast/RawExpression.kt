package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage

/**
 * Last resort!
 *
 * Try [StringLiteralExpression] or [NumericLiteralExpression]
 * Prefer all other [Expression] types.
 *
 * See [EmptyExpression]
 *
 * Only helpful when it works across languages
 */
data class RawExpression(
  private val value: String,
) : Expression {
  init {
    require(value.isNotBlank()) { "expression cannot be empty" }
  }

  /** Assume text is already appropriate for target language */
  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = value
}
