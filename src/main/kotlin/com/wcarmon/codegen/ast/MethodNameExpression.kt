package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

data class MethodNameExpression(
  val name: Name,
) : Expression {

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = when (targetLanguage) {
    C_17 -> TODO()

    CPP_14,
    CPP_17,
    CPP_20,
    -> TODO()

    DART_2 -> TODO()

    GOLANG_1_7,
    JAVA_08,
    JAVA_11,
    JAVA_17,
    KOTLIN_JVM_1_4,
    -> name.lowerCamel

    PROTOCOL_BUFFERS_3 -> TODO()
    PYTHON_3 -> TODO()
    RUST_1_54 -> TODO()
    SQL -> TODO()
    SWIFT_5 -> TODO()
    TYPESCRIPT_4 -> TODO()
  } + targetLanguage.statementTerminatorLiteral(terminate)
}
