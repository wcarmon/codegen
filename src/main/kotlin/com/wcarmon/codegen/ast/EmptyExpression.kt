package com.wcarmon.codegen.ast

object EmptyExpression : Expression {

  override val expressionName = EmptyExpression::class.java.name

  override fun render(config: RenderConfig) = ""
}
