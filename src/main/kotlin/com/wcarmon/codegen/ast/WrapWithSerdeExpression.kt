package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Serde
import com.wcarmon.codegen.model.SerdeMode
import com.wcarmon.codegen.model.TargetLanguage

//TODO: document me
data class WrapWithSerdeExpression(
  private val serde: Serde,
  private val serdeMode: SerdeMode,
  private val wrapped: Expression,
) : Expression {

  private val wrappedExpression: WrappedExpression =
    WrappedExpression(
      wrapped = wrapped,
      wrapperTemplate = serde.forMode(serdeMode)
    )

  //TODO: should I use lineIndentation?
  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ) = wrappedExpression.render(targetLanguage, terminate)
}
