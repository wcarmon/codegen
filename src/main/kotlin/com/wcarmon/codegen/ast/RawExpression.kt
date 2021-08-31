package com.wcarmon.codegen.ast

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

  override val expressionName = RawExpression::class.java.simpleName

  /** Assume text is already appropriate for target language */
  override fun renderWithoutDebugComments(config: RenderConfig) = value
}
