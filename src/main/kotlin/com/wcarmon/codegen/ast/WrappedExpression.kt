package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.StringFormatTemplate
import com.wcarmon.codegen.model.TargetLanguage

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

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ) = wrapperTemplate.expand(
    wrapped.render(targetLanguage, false))
}
