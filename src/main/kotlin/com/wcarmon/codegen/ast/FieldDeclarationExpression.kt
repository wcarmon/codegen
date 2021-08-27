package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage

data class FieldDeclarationExpression(

  val finalityModifier: FinalityModifier = FinalityModifier.FINAL,

  //TODO: field name expression?
  val name: Name,

  val visibilityModifier: VisibilityModifier = VisibilityModifier.PRIVATE,

  val defaultValue: Expression? = null,

  val type: LogicalFieldType,
) : Expression {

  override fun serialize(targetLanguage: TargetLanguage, terminate: Boolean): String {
    TODO("Not yet implemented")
  }
}
