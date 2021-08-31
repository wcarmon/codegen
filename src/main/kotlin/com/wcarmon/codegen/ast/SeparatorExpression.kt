package com.wcarmon.codegen.ast

object SeparatorExpression : Expression {

  override val expressionName = SeparatorExpression::class.java.name

  override fun render(config: RenderConfig) = "\n"
}
