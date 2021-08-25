package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Parameters are declared
 * Arguments are passed & read at runtime
 */
data class MethodParameterExpression(
  val finalityModifier: FinalityModifier = FinalityModifier.FINAL,
  val name: Name,
  val type: LogicalFieldType,
) : Expression {

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) =
    when (targetLanguage) {
      C_17 -> TODO()
      CPP_14,
      CPP_17,
      CPP_20,
      -> TODO()

      DART_2 -> TODO()

      GOLANG_1_7 -> TODO()

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava()

      KOTLIN_JVM_1_4 -> handleKotlin()

      PROTOCOL_BUFFERS_3 -> TODO()

      PYTHON_3 -> TODO()

      RUST_1_54 -> TODO()

      SQL -> TODO()

      SWIFT_5 -> TODO()

      TYPESCRIPT_4 -> TODO()
    }


  private fun handleJava(): String {
    TODO("Not yet implemented")
  }

  private fun handleKotlin(): String {
    TODO("Not yet implemented")
  }
}
