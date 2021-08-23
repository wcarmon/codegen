package com.wcarmon.codegen.model.view

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.buildSerdeReadExpression

/**
 * Java related convenience methods for a [Field]
 */
data class JavaField(
  private val field: Field,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isJava) {
      "invalid target language: $targetLanguage"
    }
  }

  val readFromProtoExpression by lazy {
    buildSerdeReadExpression(
      field = field,
      fieldReadPrefix = "",
      fieldReadStyle = targetLanguage.fieldReadStyle,
      serdeMode = DESERIALIZE
    ).serialize(targetLanguage)
  }

  val readForProtoExpression by lazy {
    buildSerdeReadExpression(
      field = field,
      fieldReadPrefix = "",
      fieldReadStyle = targetLanguage.fieldReadStyle,
      serdeMode = SERIALIZE
    ).serialize(targetLanguage)
  }
}
