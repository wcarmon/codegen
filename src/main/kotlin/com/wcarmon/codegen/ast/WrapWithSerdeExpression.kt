package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Serde
import com.wcarmon.codegen.model.SerdeMode
import com.wcarmon.codegen.model.TargetLanguage

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

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = wrappedExpression.serialize(targetLanguage, terminate)
}
