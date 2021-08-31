package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Aka ValidationExpression
 */
class PreconditionExpression : Expression {

  override val expressionName = PreconditionExpression::class.java.name

  override fun render(config: RenderConfig) =
    when (config.targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4 -> handleKotlin(config)

      else -> TODO()
    }

  //TODO: just core java (no guava)
  private fun handleJava(
    config: RenderConfig,
  ): String {
    TODO("Not yet implemented")
  }

  private fun handleKotlin(
    config: RenderConfig,
  ): String {
    TODO("Not yet implemented")
  }
}
