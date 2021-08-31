package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * If or If-Else expression
 *
 * For Java: a Conditional Statement
 * For Kotlin: a Conditional Expression
 */
data class ConditionalExpression(
  private val condition: Expression,
  private val expressionForTrue: Expression,
  private val expressionForFalse: Expression = EmptyExpression,
) : Expression {

  override val expressionName = ConditionalExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig) = when (config.targetLanguage) {
    C_17,
    CPP_14,
    CPP_17,
    CPP_20,
    DART_2,
    JAVA_08,
    JAVA_11,
    JAVA_17,
    TYPESCRIPT_4,
    -> cStyle(config)

    KOTLIN_JVM_1_4,
    -> cStyle(config.unterminated)

    GOLANG_1_7,
    RUST_1_54,
    SWIFT_5,
    -> noParens(config.unterminated)

    PYTHON_3 -> pythonStyle(config)

    PROTOCOL_BUFFERS_3,
    -> TODO("Conditionals not supported on $config")

    else -> TODO("Conditionals not supported on $config")
  }

  //TODO: use lineIndentation
  private fun cStyle(config: RenderConfig): String =
    if (expressionForFalse.isBlank(config)) {
      """
      |if (${condition.render(config.unterminated)}) {
      |  ${expressionForTrue.render(config)}   
      |}
      |
      """

    } else {
      """
      |if (${condition.render(config.unterminated)}) {
      |  ${expressionForTrue.render(config)}   
      |} else {
      |  ${expressionForFalse.render(config)}
      |}
      |
      """
    }.trimMargin()

  private fun noParens(config: RenderConfig)
      : String = if (expressionForFalse.isBlank(config)) {
    """
      |if ${condition.render(config.unterminated)} {
      |  ${expressionForTrue.render(config)}
      |}
      |
      """
  } else {
    """
      |if ${condition.render(config.unterminated)} {
      |  ${expressionForTrue.render(config)}
      |} else {
      |  ${expressionForFalse.render(config)}
      |}
      |
      """
  }.trimMargin()

  private fun pythonStyle(config: RenderConfig) =
    if (expressionForFalse.isBlank(config)) {
      """
      |if ${condition.render(config.unterminated)}:
      |  ${expressionForTrue.render(config.unterminated)}
      |
      """
    } else {
      """
      |if ${condition.render(config.unterminated)}:
      |  ${expressionForTrue.render(config.unterminated)}
      |else:          
      |  ${expressionForFalse.render(config.unterminated)}
      |
      """
    }.trimMargin()
}
