package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.ast.NumericLiteralBase.*

data class NumericLiteralExpression(
  val base: NumericLiteralBase = NumericLiteralBase.DECIMAL,
  val value: Number,
) : Expression {

  override fun serialize(
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
