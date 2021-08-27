package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage

/**
 * Last resort!
 *
 * Try [StringLiteralExpression] or [NumericLiteralExpression]
 * Prefer all other [Expression] types.
 *
 * Only helpful when it works across languages
 */
data class RawExpression(
  val value: String,
) : Expression {
  init {
    require(value.isNotBlank())
  }

  /** Assume text is already appropriate for target language */
  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = value
}
