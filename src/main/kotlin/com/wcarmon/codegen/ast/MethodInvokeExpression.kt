package com.wcarmon.codegen.ast

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

  override val expressionName: String = MethodInvokeExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) =
    when (config.targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4,
      -> handleKotlin(config)

      else -> TODO()
    }

  private fun handleJava(config: RenderConfig): String {
    val prefix = buildFieldOwnerPrefix(config)
    val suffix = config.targetLanguage.statementTerminatorLiteral(config.terminate)

    val method = methodName.render(config)

    val csvArgs = arguments
      .joinToString(",") {
        it.render(config.unindented.unterminated)
      }

    return config.lineIndentation +
        prefix +
        method +
        "(" +
        csvArgs +
        ")" +
        suffix
  }

  private fun handleKotlin(config: RenderConfig): String =
    handleJava(config.unterminated)

  private fun buildFieldOwnerPrefix(config: RenderConfig): String {
    val ownerPrefix = fieldOwner.render(config.unindented.unterminated)
    if (ownerPrefix.isBlank()) {
      return ""
    }

    return "${ownerPrefix}."
  }
}
