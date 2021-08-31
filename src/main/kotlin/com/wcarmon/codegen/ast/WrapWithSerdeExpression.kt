package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Serde
import com.wcarmon.codegen.model.SerdeMode

//TODO: document me
data class WrapWithSerdeExpression(
  private val serde: Serde,
  private val serdeMode: SerdeMode,
  private val wrapped: Expression,
) : Expression {

  override val expressionName = WrapWithSerdeExpression::class.java.name

  private val wrappedExpression: WrappedExpression =
    WrappedExpression(
      wrapped = wrapped,
      wrapperTemplate = serde.forMode(serdeMode)
    )

  override fun render(config: RenderConfig) = wrappedExpression.render(config)
}
