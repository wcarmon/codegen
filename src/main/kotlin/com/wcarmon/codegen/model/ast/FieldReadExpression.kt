package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.FieldReadStyle
import com.wcarmon.codegen.model.FieldReadStyle.DIRECT
import com.wcarmon.codegen.model.FieldReadStyle.GETTER
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * TODO: document class
 *
 * @param fieldReadPrefix   //TODO: document me
 */
data class FieldReadExpression(
  val fieldName: Name,

  val assertNonNull: Boolean = false,
  val fieldReadPrefix: String = "",
  val overrideFieldReadStyle: FieldReadStyle? = null,
) : Expression {

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) =
    fieldReadPrefix +
        when (getFieldReadStyle(targetLanguage)) {
          DIRECT -> getDirectFieldName(targetLanguage) + nonNullSnippet
          GETTER -> "get${fieldName.upperCamel}()"
        } +
        serializeTerminator(terminate)

  // Kotlin non-null assertion
  private val nonNullSnippet by lazy {
    if (assertNonNull) "!!"
    else ""
  }

  private fun getDirectFieldName(targetLanguage: TargetLanguage) =
    when (targetLanguage) {
      GOLANG_1_7,
      JAVA_08,
      JAVA_11,
      JAVA_17,
      KOTLIN_JVM_1_4,
      TYPESCRIPT_4,
      -> fieldName.lowerCamel

      SQL -> fieldName.lowerSnake

      C_17,
      CPP_14,
      CPP_17,
      CPP_20,
      DART_2,
      PROTOCOL_BUFFERS_3,
      PYTHON_3,
      RUST_1_54,
      SWIFT_5,
      -> TODO("what is the field read naming idiom for $targetLanguage")
    }

  private fun getFieldReadStyle(targetLanguage: TargetLanguage) =
    overrideFieldReadStyle ?: defaultFieldReadStyle(targetLanguage)

  private fun defaultFieldReadStyle(targetLanguage: TargetLanguage) =
    when (targetLanguage) {
      C_17,
      CPP_14,
      CPP_17,
      CPP_20,
      GOLANG_1_7,
      KOTLIN_JVM_1_4,
      PROTOCOL_BUFFERS_3,
      PYTHON_3,
      RUST_1_54,
      SQL,
      TYPESCRIPT_4,
      -> DIRECT

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> GETTER

      DART_2 -> TODO("What is a good default FieldReadStyle for Dart?")
      SWIFT_5 -> TODO("What is a good default FieldReadStyle for Swift?")
    }
}
