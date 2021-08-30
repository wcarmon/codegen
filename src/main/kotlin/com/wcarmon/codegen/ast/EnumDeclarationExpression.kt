package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage

data class EnumDeclarationExpression(
  private val name: Name,
  //TODO: values
  //TODO: fields
  //TODO: field validation
  //TODO: lookup
) : Expression {

  override fun render(targetLanguage: TargetLanguage, terminate: Boolean): String {
    TODO("Not yet implemented")
  }
}
