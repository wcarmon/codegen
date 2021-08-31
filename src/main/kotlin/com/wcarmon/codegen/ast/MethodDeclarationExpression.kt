package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage.*

data class MethodDeclarationExpression(
  private val header: MethodHeaderExpression,

  private val extraPreconditions: List<FieldValidationExpressions> = listOf(),

  private val body: List<Expression>,
) : Expression {

  override val expressionName = MethodDeclarationExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ): String = when (config.targetLanguage) {
    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> handleJava(config)

    KOTLIN_JVM_1_4 -> handleKotlin(config)

    else -> TODO()
  }

  private fun handleJava(config: RenderConfig): String {
    TODO("Not yet implemented")
  }

  private fun handleKotlin(config: RenderConfig): String {
    TODO("Not yet implemented")
  }
}


