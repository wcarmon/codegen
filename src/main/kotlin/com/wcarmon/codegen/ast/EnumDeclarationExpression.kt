package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name

data class EnumDeclarationExpression(
  private val name: Name,
  //TODO: values
  //TODO: fields
  //TODO: field validation
  //TODO: lookup
) : Expression {

  override val expressionName = EnumDeclarationExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig): String {
    TODO("Not yet implemented")
  }
}
