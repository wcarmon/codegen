package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.QuoteType
import com.wcarmon.codegen.model.TargetLanguage

data class StringLiteralExpression(
  private val value: String,

  private val quoteType: QuoteType = QuoteType.DOUBLE,
) : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ) =
    quoteType.wrap(value) +
        targetLanguage.statementTerminatorLiteral(terminate)
}

