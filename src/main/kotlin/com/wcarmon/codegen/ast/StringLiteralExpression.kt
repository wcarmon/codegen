package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.QuoteType

data class StringLiteralExpression(
  private val value: String,

  private val quoteType: QuoteType = QuoteType.DOUBLE,
) : Expression {

  override val expressionName = StringLiteralExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig) =
    quoteType.wrap(value) + config.statementTerminatorLiteral
}

