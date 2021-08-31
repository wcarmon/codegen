package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name

data class EnumDeclarationExpression(
  private val name: Name,
  //TODO: values
  //TODO: fields
  //TODO: field validation
  //TODO: lookup
) : Expression {

  override val expressionName = EnumDeclarationExpression::class.java.name

  override fun render(config: RenderConfig): String {
    TODO("Not yet implemented")
  }
}
