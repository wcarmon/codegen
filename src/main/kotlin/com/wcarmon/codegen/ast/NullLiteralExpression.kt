package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage.GOLANG_1_9
import com.wcarmon.codegen.model.TargetLanguage.PYTHON_3

/**
 * c:           NULL
 * golang:      nil
 * groovy:      null
 * java:        null
 * javascript:  null
 * kotlin:      null
 * python:      None
 * RDBMS:       NULL
 * rust:        std::ptr::null    <--- TODO: verify this
 * typescript:  null
 */
object NullLiteralExpression : Expression {

  override val expressionName: String = NumericLiteralExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) = config.lineIndentation +
      when (config.targetLanguage) {
        GOLANG_1_9 -> "nil"
        PYTHON_3 -> "None"
        else -> "null"
      } +
      config.statementTerminatorLiteral
}
