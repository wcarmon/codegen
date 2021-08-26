package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.QuoteType
import com.wcarmon.codegen.model.TargetLanguage

data class StringLiteralExpression(
  val quoteType: QuoteType = QuoteType.DOUBLE,
  val value: String,
) : Expression {

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) =
    quoteType.wrap(value) +
        targetLanguage.statementTerminatorLiteral(terminate)
}

