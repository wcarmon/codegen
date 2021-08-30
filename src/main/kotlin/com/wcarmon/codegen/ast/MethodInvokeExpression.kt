package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * See also [FieldReadExpression]
 */
data class MethodInvokeExpression(
  private val arguments: List<Expression> = listOf(),

  /** Kotlin allows non-null assertion (eg. !!) */
  private val assertNonNull: Boolean = false,

  private val methodName: MethodNameExpression,

  /**
   * eg. "entity."
   * null implies fieldOwner==this
   * */
  private val fieldOwner: Expression = EmptyExpression,
) : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
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
      -> handleJava(targetLanguage,
        terminate,
        lineIndentation)

      KOTLIN_JVM_1_4,
      -> handleKotlin(
        targetLanguage,
        lineIndentation)

      PROTOCOL_BUFFERS_3 -> TODO()

      PYTHON_3 -> TODO()

      RUST_1_54 -> TODO()

      SQL_DB2 -> TODO()
      SQL_H2 -> TODO()
      SQL_MARIA -> TODO()
      SQL_MYSQL -> TODO()
      SQL_ORACLE -> TODO()
      SQL_POSTGRESQL -> TODO()
      SQL_SQLITE -> TODO()

      SWIFT_5 -> TODO()

      TYPESCRIPT_4 -> TODO()
    }

  private fun handleJava(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ): String {
    val prefix = buildFieldOwnerPrefix(targetLanguage)
    val suffix = targetLanguage.statementTerminatorLiteral(terminate)

    val method = methodName.render(targetLanguage)

    val csvArgs = arguments
      .joinToString(",") {
        it.render(targetLanguage, false, "")
      }

    return lineIndentation +
        prefix +
        method +
        "(" +
        csvArgs +
        ")" +
        suffix
  }

  private fun handleKotlin(
    targetLanguage: TargetLanguage,
    lineIndentation: String,
  ): String =
    handleJava(
      targetLanguage,
      false,
      lineIndentation)

  private fun buildFieldOwnerPrefix(targetLanguage: TargetLanguage): String {
    val ownerPrefix = fieldOwner.render(targetLanguage, false, "")
    if (ownerPrefix.isBlank()) {
      return ""
    }

    return "${ownerPrefix}."
  }
}