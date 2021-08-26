package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.StringFormatTemplate.Companion.INLINE
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
  val wrapped: Expression,

  /**
   * When %s is missing, [wrapped] is ignored
   */
  val wrapperTemplate: StringFormatTemplate = INLINE,
) : Expression {

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = wrapperTemplate.expand(
    wrapped.serialize(targetLanguage, false))
}

