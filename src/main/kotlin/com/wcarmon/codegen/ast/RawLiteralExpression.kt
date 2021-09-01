package com.wcarmon.codegen.ast

/**
 * No processing
 * Last resort!
 * Prefer all other [Expression] types.
 * Try [StringLiteralExpression] or [NumericLiteralExpression]
 *
 * See [EmptyExpression]
 *
 * Only helpful when it works across languages
 */
data class RawLiteralExpression(
  private val value: String,
) : Expression {
  init {
    require(value.isNotBlank()) { "expression cannot be empty" }
  }

  override val expressionName: String = RawLiteralExpression::class.java.simpleName

  /** Assume text is already appropriate for target language */
  override fun renderWithoutDebugComments(config: RenderConfig) = value
}
