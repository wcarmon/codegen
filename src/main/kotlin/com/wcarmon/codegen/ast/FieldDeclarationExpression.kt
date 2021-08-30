package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage

data class FieldDeclarationExpression(

  private val finalityModifier: FinalityModifier = FinalityModifier.FINAL,

  private val name: Name,

  private val visibilityModifier: VisibilityModifier = VisibilityModifier.PRIVATE,

  private val defaultValue: Expression? = null,

  private val type: LogicalFieldType,
) : Expression {

  override fun render(targetLanguage: TargetLanguage, terminate: Boolean): String {
    TODO("Not yet implemented")
  }
}
