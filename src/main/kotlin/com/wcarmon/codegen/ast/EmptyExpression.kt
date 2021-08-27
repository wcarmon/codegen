package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage

object EmptyExpression : Expression {
  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = ""
}
