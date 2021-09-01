package com.wcarmon.codegen.ast

object EmptyExpression : Expression {

  override val expressionName: String = EmptyExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig) = ""
}
