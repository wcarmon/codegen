package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Documentation
import com.wcarmon.codegen.model.Documentation.Companion.EMPTY
import com.wcarmon.codegen.model.PackageName
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

data class InterfaceDeclarationExpression(

  val documentation: Documentation = EMPTY,

  val fullyQualifiedInterfaces: List<String>,

  val instanceMethods: List<MethodHeaderExpression> = listOf(),

  val defaultMethods: List<MethodDeclarationExpression> = listOf(),

  val name: Name,

  val packageName: PackageName,

  //TODO: List: generic parameters
) : Expression {

  val isFunctional by lazy {
    instanceMethods.size == 1
  }

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ): String {

    return when (targetLanguage) {
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

      KOTLIN_JVM_1_4,
      -> handleKotlin()

      PROTOCOL_BUFFERS_3 -> TODO()

      PYTHON_3 -> TODO()

      RUST_1_54 -> TODO()

      SQL -> TODO()

      SWIFT_5 -> TODO()

      TYPESCRIPT_4 -> TODO()
    }
  }

  private fun handleJava(): String {
    TODO("Not yet implemented")
  }

  private fun handleKotlin(): String {
    TODO("Not yet implemented")
  }

}
