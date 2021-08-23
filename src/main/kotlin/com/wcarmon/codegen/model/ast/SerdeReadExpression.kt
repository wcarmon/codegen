package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.ExpressionTemplate
import com.wcarmon.codegen.model.Serde
import com.wcarmon.codegen.model.SerdeMode
import com.wcarmon.codegen.model.TargetLanguage

/**
 * Conditionally wraps any [Expression]
 * (Typically a [FieldReadExpression])
 *
 * Allows User defined Serialize or Deserialize template
 * Allows arbitrary type conversion
 *
 * See [com.wcarmon.codegen.model.Serde]
 * See [com.wcarmon.codegen.model.ExpressionTemplate]
 */
data class SerdeReadExpression(

  /**
   * Fills the placeholder in the serde template
   * Normally a [FieldReadExpression], but not required
   */
  val fieldReadExpression: Expression,

  /**
   * Either a Serialize or a Deserialize template
   * See docs on [ExpressionTemplate]
   */
  val serdeTemplate: ExpressionTemplate,

  ) : Expression {

  companion object {

    fun forSerde(
      serde: Serde,
      mode: SerdeMode,
      fieldReadExpression: Expression,
    ) = SerdeReadExpression(
      fieldReadExpression = fieldReadExpression,
      serdeTemplate = serde.forMode(mode),
    )
  }

  /**
   * Wraps the fieldReadExpression in the serde expression
   */
  override fun serialize(targetLanguage: TargetLanguage, terminate: Boolean) =
    serdeTemplate
      .expand(
        fieldReadExpression.serialize(
          targetLanguage,
          false))
      .serialize(
        targetLanguage,
        terminate)
}
