package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage.*

data class MethodNameExpression(
  private val name: Name,
) : Expression {

  override val expressionName: String = MethodNameExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) = when (config.targetLanguage) {
    GOLANG_1_7,
    JAVA_08,
    JAVA_11,
    JAVA_17,
    KOTLIN_JVM_1_4,
    -> name.lowerCamel

    PROTOCOL_BUFFERS_3 -> TODO()
    PYTHON_3 -> TODO()
    RUST_1_54 -> TODO()
    SWIFT_5 -> TODO()
    TYPESCRIPT_4 -> TODO()

    else -> TODO("finish rendering method name exp")
  } + config.statementTerminatorLiteral
}
