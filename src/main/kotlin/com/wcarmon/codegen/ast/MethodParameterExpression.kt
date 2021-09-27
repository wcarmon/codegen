package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Parameters are declared
 * Arguments are passed & read at runtime
 */
data class MethodParameterExpression(
  private val field: Field,

  private val fullyQualified: Boolean = true,
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

      GOLANG_1_9 -> handleGolang(config)

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

    parts += field.effectiveTypeLiteral(targetLanguage = config.targetLanguage, fullyQualified)
    parts += field.name.lowerCamel

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

    parts += field.name.lowerCamel + ":"
    parts += field.effectiveTypeLiteral(config.targetLanguage, fullyQualified = fullyQualified)

    return parts.joinToString(
      separator = " ",
    )
  }

  private fun handleGolang(config: RenderConfig): String {
    val parts = mutableListOf<String>()

    parts += field.name.lowerCamel
    parts += field.effectiveTypeLiteral(config.targetLanguage, fullyQualified = fullyQualified)

    return parts.joinToString(
      separator = " ",
    )
  }
}
