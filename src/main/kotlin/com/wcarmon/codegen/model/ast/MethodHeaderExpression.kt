package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.Documentation
import com.wcarmon.codegen.model.Documentation.Companion.EMPTY
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Signature for a method/function
 */
data class MethodHeaderExpression(
  val documentation: Documentation = EMPTY,

  val finalityModifier: FinalityModifier = FinalityModifier.NON_FINAL,

  val name: MethodNameExpression,

  val override: Boolean = false,

  val parameters: List<MethodParameterExpression> = listOf(),

  /**
   * Java, C, C++, Rust, Typescript, JS, Python, Dart, ... only support 1 return type
   */
  //TODO: golang & Lua support multiple returns
  val returnType: LogicalFieldType,

  val visibilityModifier: VisibilityModifier = VisibilityModifier.PUBLIC,

  //TODO: List: Generic parameter(s)  eg. "<T: Bacon, S>"
) : Expression {

  override fun serialize(targetLanguage: TargetLanguage, terminate: Boolean) =
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
