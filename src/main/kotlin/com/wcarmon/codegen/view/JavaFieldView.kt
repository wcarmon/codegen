package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RawExpression
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.*
import com.wcarmon.codegen.util.defaultValueLiteralForJVM
import com.wcarmon.codegen.util.javaTypeLiteral

/**
 * Java related convenience methods for a [Field]
 */
data class JavaFieldView(
  private val field: Field,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isJava) {
      "invalid target language: $targetLanguage"
    }
  }

  val type = javaTypeLiteral(field.type, true)

  val defaultValueLiteralForJVM: String? by lazy {
    defaultValueLiteralForJVM(field)
  }

  fun equalityExpression(
    identifier0: String = "this",
    identifier1: String = "that",
  ) = com.wcarmon.codegen.util.javaEqualityExpression(
    field.type,
    field.name,
    identifier0,
    identifier1
  ).serialize(targetLanguage)

  val resultSetGetterExpression by lazy {
    buildResultSetGetterExpression(field)
      .serialize(targetLanguage)
  }


  fun readFromProtoExpression(fieldReadPrefix: String): String =
  //TODO: for collections, reading proto fields requires "proto.getFooList()"
    //  Use a "findProtoGetter" method, like I did for jdbc
    buildSerdeReadExpression(
      field = field,
      fieldReadPrefix = fieldReadPrefix,
      fieldReadStyle = targetLanguage.fieldReadMode,
      serdeMode = DESERIALIZE
    ).serialize(targetLanguage)

  fun readForProtoExpression(fieldReadPrefix: String): String =
    //TODO: use protoBuilderGetter
    buildSerdeReadExpression(
      field = field,
      fieldReadPrefix = fieldReadPrefix,
      fieldReadStyle = targetLanguage.fieldReadMode,
      serdeMode = SERIALIZE
    ).serialize(targetLanguage)


  //TODO: test this on types that are already unqualified
  val unqualifiedType = javaTypeLiteral(field.type, false)

  //TODO: convert to fun,
  //    accept template placeholder replacement here
  //    rename
  val unmodifiableCollectionMethod by lazy {
    unmodifiableJavaCollectionMethod(field.effectiveBaseType)
  }

  val protoSerializeExpressionForTypeParameters by lazy {
    protoReadExpressionForTypeParameters(
      field,
      listOf(RawExpression("item")),
      SERIALIZE)
      .map {
        it.serialize(targetLanguage)
      }
  }

  val protoDeserializeExpressionForTypeParameters by lazy {
    protoReadExpressionForTypeParameters(
      field,
      listOf(RawExpression("item")),
      DESERIALIZE)
      .map {
        it.serialize(targetLanguage)
      }
  }

  // GOTCHA: Only invoke on collection types
  fun newCollectionExpression() = newJavaCollectionExpression(field.type)
}


