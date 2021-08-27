package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage

object SeparatorExpression : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = "\n"
}
