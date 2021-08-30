package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.GOLANG_1_7
import com.wcarmon.codegen.model.TargetLanguage.PYTHON_3

object NullLiteralExpression : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ) = lineIndentation +
      when (targetLanguage) {
        GOLANG_1_7 -> "nil"
        PYTHON_3 -> "None"
        else -> "null"
      } +
      targetLanguage.statementTerminatorLiteral(terminate)
}
