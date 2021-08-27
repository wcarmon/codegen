package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.NumericLiteralBase.*
import com.wcarmon.codegen.model.TargetLanguage

data class NumericLiteralExpression(
  val base: NumericLiteralBase = NumericLiteralBase.DECIMAL,
  val value: Number,
) : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
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
