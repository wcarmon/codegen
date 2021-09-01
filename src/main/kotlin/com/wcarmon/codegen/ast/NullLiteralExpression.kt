package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage.GOLANG_1_7
import com.wcarmon.codegen.model.TargetLanguage.PYTHON_3

object NullLiteralExpression : Expression {

  override val expressionName: String = NumericLiteralExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) = config.lineIndentation +
      when (config.targetLanguage) {
        GOLANG_1_7 -> "nil"
        PYTHON_3 -> "None"
        else -> "null"
      } +
      config.statementTerminatorLiteral
}
