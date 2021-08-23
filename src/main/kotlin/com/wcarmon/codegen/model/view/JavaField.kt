package com.wcarmon.codegen.model.view

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.*

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

  fun equalityExpression(
    identifier0: String = "this",
    identifier1: String = "that",
  ) = com.wcarmon.codegen.model.util.javaEqualityExpression(
    field.type,
    field.name,
    identifier0,
    identifier1
  ).serialize(targetLanguage)

  val resultSetGetterExpression by lazy {
    buildResultSetGetterExpression(field)
      .serialize(targetLanguage)
  }

  val type = javaTypeLiteral(field.type, true)

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

  //TODO: test this on types that are already unqualified
  val unqualifiedType = javaTypeLiteral(field.type, false)

  //TODO: convert to fun,
  //    accept template placeholder replacement here
  //    rename
  val unmodifiableCollectionMethod by lazy {
    unmodifiableJavaCollectionMethod(field.effectiveBaseType)
  }

  // GOTCHA: Only invoke on collection types
  fun newCollectionExpression() = newJavaCollectionExpression(field.type)
}
