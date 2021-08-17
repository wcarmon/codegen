package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

//TODO: document me
data class NullComparisonExpression(
  val compareTo: Expression,
) : Expression {

  //NOTE: termination not supported
  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) =
    when (targetLanguage) {
      CPP_14,
      CPP_17,
      CPP_20,
      -> TODO()

      DART_2 -> TODO()

      C_17 -> "NULL == " + compareTo.serialize(targetLanguage, false)
      GOLANG_1_7 -> "nil == " + compareTo.serialize(targetLanguage, false)

      JAVA_08,
      JAVA_11,
      JAVA_17,
      KOTLIN_JVM_1_4,
      -> "null == " + compareTo.serialize(targetLanguage, false)

      PYTHON_3 -> TODO()  // foo is None
      RUST_1_54 -> TODO()
      SQL -> TODO()
      SWIFT_5 -> TODO()
      TYPESCRIPT_4 -> TODO()  // == null || undefined
    }
}
