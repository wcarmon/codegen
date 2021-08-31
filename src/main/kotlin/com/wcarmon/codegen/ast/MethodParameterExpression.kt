package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Parameters are declared
 * Arguments are passed & read at runtime
 */
data class MethodParameterExpression(

  private val finalityModifier: FinalityModifier = FinalityModifier.FINAL,

  private val name: Name,

  private val type: LogicalFieldType,
) : Expression {

  override val expressionName = MethodParameterExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) =
    when (config.targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4 -> handleKotlin(config)

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
