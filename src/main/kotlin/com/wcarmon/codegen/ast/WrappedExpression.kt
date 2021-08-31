package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.StringFormatTemplate

/**
 * Wraps an [Expression]
 *
 * Uses serialized [Expression] to fill placeholder(s) in template
 */
data class WrappedExpression(

  /**
   * Wrapped by [wrapperTemplate]
   */
  private val wrapped: Expression,

  /**
   * When %s is missing, [wrapped] is ignored
   */
  private val wrapperTemplate: StringFormatTemplate = StringFormatTemplate.INLINE,
) : Expression {

  override val expressionName = WrappedExpression::class.java.name

  override fun render(config: RenderConfig) =
    wrapperTemplate.expand(
      wrapped.render(config.unterminated))
}
