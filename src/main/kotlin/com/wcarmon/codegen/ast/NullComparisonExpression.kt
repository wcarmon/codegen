package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Examples:
 *  null == x
 *  nil == x
 */
data class NullComparisonExpression(
  private val compareToMe: Expression,
) : Expression {

  //NOTE: termination not supported
  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ) =
    when (targetLanguage) {
      CPP_14,
      CPP_17,
      CPP_20,
      -> "nullptr == " + compareToMe.render(
        targetLanguage,
        false,
        lineIndentation)

      C_17,
      -> "NULL == " + compareToMe.render(
        targetLanguage,
        false,
        lineIndentation)

      GOLANG_1_7,
      -> "nil == " + compareToMe.render(
        targetLanguage,
        false,
        lineIndentation)

      DART_2,
      JAVA_08,
      JAVA_11,
      JAVA_17,
      KOTLIN_JVM_1_4,
      -> "null == " + compareToMe.render(
        targetLanguage,
        false,
        lineIndentation)

      PROTOCOL_BUFFERS_3 -> TODO()
      PYTHON_3 -> TODO()  // foo is None
      RUST_1_54 -> TODO()

      SWIFT_5 -> TODO()

      TYPESCRIPT_4 -> TODO()  // == null || undefined

      SQL_DB2 -> TODO()
      SQL_H2 -> TODO()
      SQL_MARIA -> TODO()
      SQL_MYSQL -> TODO()
      SQL_ORACLE -> TODO()
      SQL_POSTGRESQL -> TODO()
      SQL_SQLITE -> TODO()
    }
}
