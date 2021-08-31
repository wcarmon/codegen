package com.wcarmon.codegen.ast

object SeparatorExpression : Expression {

  override val expressionName = SeparatorExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig) = "\n"
}
