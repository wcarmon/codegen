package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * See also [FieldReadExpression]
 */
data class MethodInvokeExpression(
  val arguments: List<Expression> = listOf(),

  /** Kotlin allows non-null assertion (eg. !!) */
  val assertNonNull: Boolean = false,

  val methodName: MethodNameExpression,

  /**
   * eg. "entity."
   * null implies fieldOwner==this
   * */
  val fieldOwner: Expression? = null,
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
      -> handleJava(targetLanguage, terminate)

      KOTLIN_JVM_1_4,
      -> handleKotlin(targetLanguage)

      PROTOCOL_BUFFERS_3 -> TODO()

      PYTHON_3 -> TODO()

      RUST_1_54 -> TODO()

      SQL -> TODO()

      SWIFT_5 -> TODO()

      TYPESCRIPT_4 -> TODO()
    }

  private fun handleJava(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ): String {
    val prefix = buildFieldOwnerPrefix(targetLanguage)
    val suffix = targetLanguage.statementTerminatorLiteral(terminate)

    val method = methodName.serialize(targetLanguage)

    val csvArgs = arguments
      .joinToString(",") {
        it.serialize(targetLanguage, false)
      }

    return "$prefix${method}($csvArgs)$suffix"
  }

  private fun handleKotlin(targetLanguage: TargetLanguage): String =
    handleJava(targetLanguage, false)

  private fun buildFieldOwnerPrefix(targetLanguage: TargetLanguage): String {
    val ownerPrefix = fieldOwner?.serialize(targetLanguage, false) ?: ""
    if (ownerPrefix.isBlank()) {
      return ""
    }

    return "$ownerPrefix."
  }
}
