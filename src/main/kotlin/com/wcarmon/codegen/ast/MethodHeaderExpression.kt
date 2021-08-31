package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Signature for a method/function
 */
data class MethodHeaderExpression(
  private val documentation: DocumentationExpression = DocumentationExpression.EMPTY,

  private val finalityModifier: FinalityModifier = FinalityModifier.NON_FINAL,

  private val name: MethodNameExpression,

  private val override: Boolean = false,

  private val parameters: List<MethodParameterExpression> = listOf(),

  /**
   * Java, C, C++, Rust, Typescript, JS, Python, Dart, ... only support 1 return type
   */
  //TODO: golang & Lua support multiple returns
  private val returnType: LogicalFieldType,

  private val visibilityModifier: VisibilityModifier = VisibilityModifier.PUBLIC,

  //TODO: List: Generic parameter(s)  eg. "<T: Bacon, S>"
) : Expression {

  override val expressionName = MethodHeaderExpression::class.java.name

  override fun render(
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
    TODO("Not yet implemented")
  }

  private fun handleKotlin(config: RenderConfig): String {
    TODO("Not yet implemented")
  }
}
