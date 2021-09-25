package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.util.javaTypeLiteral
import com.wcarmon.codegen.util.kotlinTypeLiteral

/**
 * Parameters are declared
 * Arguments are passed & read at runtime
 */
data class MethodParameterExpression(
  private val name: Name,
  private val type: LogicalFieldType,

  private val qualified: Boolean = true,
  private val annotations: Collection<AnnotationExpression> = listOf(),
  private val finalityModifier: FinalityModifier = FinalityModifier.FINAL,
) : Expression {

  override val expressionName: String = MethodParameterExpression::class.java.simpleName

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

  /**
   * eg.
   *  - @Nullable String foo
   *  - java.time.Duration foo
   *  - Long foo
   *  - Period foo
   */
  private fun handleJava(
    config: RenderConfig,
  ): String {

    val parts = mutableListOf<String>()

    parts += annotations
      .map {
        it.render(config.unterminated)
      }
      .sorted()

    parts += javaTypeLiteral(type, qualified)
    parts += name.lowerCamel

    return parts.joinToString(
      separator = " ",
    )
  }

  /**
   * eg.
   *  - @Nullable foo: String
   *  - foo: java.time.Duration
   *  - foo: Long
   *  - foo: Period
   */
  private fun handleKotlin(
    config: RenderConfig,
  ): String {

    val parts = mutableListOf<String>()

    parts += annotations
      .map {
        it.render(config.unterminated)
      }
      .sorted()

    parts += name.lowerCamel + ":"
    parts += kotlinTypeLiteral(type, qualified)

    return parts.joinToString(
      separator = " ",
    )
  }
}
