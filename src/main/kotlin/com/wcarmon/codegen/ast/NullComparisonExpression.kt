package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Examples:
 *  null == x
 *  nil == x
 */
data class NullComparisonExpression(
  private val compareToMe: Expression,
) : Expression {

  override val expressionName: String = NullComparisonExpression::class.java.simpleName

  //NOTE: termination not supported
  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) =
    when (config.targetLanguage) {
      CPP_14,
      CPP_17,
      CPP_20,
      -> "nullptr == " + compareToMe.render(config.unterminated)

      C_17,
      -> "NULL == " + compareToMe.render(config.unterminated)

      GOLANG_1_8,
      -> "nil == " + compareToMe.render(config.unterminated)

      DART_2,
      JAVA_08,
      JAVA_11,
      JAVA_17,
      KOTLIN_JVM_1_4,
      -> "null == " + compareToMe.render(config.unterminated)

      PROTOCOL_BUFFERS_3 -> TODO()
      PYTHON_3 -> TODO()  // foo is None
      RUST_1_54 -> TODO()

      TYPESCRIPT_4 -> TODO()  // == null || undefined

      else -> TODO()
    }
}
