package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.NumericLiteralBase.*
import com.wcarmon.codegen.model.TargetLanguage

data class NumericLiteralExpression(
  private val base: NumericLiteralBase = DECIMAL,
  private val value: Number,
) : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ): String {
    val t = targetLanguage.statementTerminatorLiteral(terminate)

    return when (base) {
      BINARY -> TODO("convert to hex: $value")

      //TODO: add underscores every 3 numbers
      DECIMAL -> value.toString() + t

      HEX -> TODO("convert to hex: $value")
    }
  }
}
