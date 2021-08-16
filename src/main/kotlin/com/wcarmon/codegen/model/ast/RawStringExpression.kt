package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.TargetLanguage

/**
 * Last resort.
 * Prefer the other Expression types.
 *
 * Only helpful when it works across languages
 */
data class RawStringExpression(
  val text: String,
) : Expression {

  /** Assume text is already fit for target language */
  override fun serialize(targetLanguage: TargetLanguage): String = text
}
