package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.TargetLanguage

data class EnumDeclarationExpression(
  val name: Name,
  //TODO: values
  //TODO: fields
  //TODO: field validation
  //TODO: lookup
) : Expression {

  override fun serialize(targetLanguage: TargetLanguage, terminate: Boolean): String {
    TODO("Not yet implemented")
  }
}
