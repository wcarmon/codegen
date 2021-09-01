package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.NumericLiteralBase.*

data class NumericLiteralExpression(
  private val base: NumericLiteralBase = DECIMAL,
  private val value: Number,
) : Expression {

  override val expressionName: String = NumericLiteralExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ): String {
    val t = config.statementTerminatorLiteral

    return when (base) {
      BINARY -> TODO("convert to hex: $value")

      //TODO: add underscores every 3 numbers
      DECIMAL -> value.toString() + t

      HEX -> TODO("convert to hex: $value")
    }
  }
}
