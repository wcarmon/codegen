@file:JvmName("CommonCodeUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * noop unless debug mode active
 * @return same code, same behavior, wrapped in comments to help tracing expressions
 */
fun wrapWithExpressionTracingComments(
  config: RenderConfig,
  expressionName: String,
  renderedCode: String,
): String {

  if (!config.debugMode) {
    return renderedCode
  }

  return when (config.targetLanguage) {
    C_17,
    CPP_14,
    CPP_17,
    CPP_20,
    DART_2,
    GOLANG_1_7,
    JAVA_08,
    JAVA_11,
    JAVA_17,
    KOTLIN_JVM_1_4,
    PROTOCOL_BUFFERS_3,
    RUST_1_54,
    SQL_DB2,
    SQL_H2,
    SQL_MARIA,
    SQL_MYSQL,
    SQL_ORACLE,
    SQL_POSTGRESQL,
    SQL_SQLITE,
    SWIFT_5,
    TYPESCRIPT_4,
    -> handleCStyle(
      expressionName,
      renderedCode)

    PYTHON_3 -> handlePython(
      expressionName,
      renderedCode)
  }
}

private fun handleCStyle(
  expressionName: String,
  renderedCode: String,
): String =
  "/*[[START: $expressionName]]*/ $renderedCode /*[[END: $expressionName]]*/"

private fun handlePython(
  expressionName: String,
  renderedCode: String,
): String =
  TODO("python uses line comment or triple quote")
