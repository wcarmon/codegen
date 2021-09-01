package com.wcarmon.codegen.ast

object SeparatorExpression : Expression {

  override val expressionName: String = SeparatorExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig) = "\n"
}
