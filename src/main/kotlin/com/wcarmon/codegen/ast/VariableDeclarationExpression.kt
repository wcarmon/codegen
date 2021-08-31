package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage.*

data class VariableDeclarationExpression(
  private val name: Name,
  private val type: LogicalFieldType,

  private val defaultValue: Expression? = null,
  private val finalityModifier: FinalityModifier = FinalityModifier.FINAL,
) : Expression {

  override val expressionName = VariableDeclarationExpression::class.java.name

  override fun render(
    config: RenderConfig,
  ): String = when (config.targetLanguage) {
    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> handleJava(config)

    KOTLIN_JVM_1_4,
    -> handleKotlin(config)

    else -> TODO()
  }

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
