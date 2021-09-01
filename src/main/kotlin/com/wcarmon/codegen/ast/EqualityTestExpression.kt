package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Compares two [Expression]s for equality
 *
 * Useful inside a [ConditionalExpression]
 *
 * See also [NullComparisonExpression]
 */
data class EqualityTestExpression(
  private val expression0: Expression,
  private val expression1: Expression,

  /**
   * Some languages use different comparison strategy based on type
   * (eg. Java double/array/string)
   */
  private val expressionType: LogicalFieldType,
) : Expression {

  override val expressionName: String = EqualityTestExpression::class.java.simpleName

  /**
   * eg. "Arrays.deepEquals(xArr, yArr)"
   * eg. "Arrays.deepEquals(xArr, yArr);"
   * eg. "Double.compare(x, y)"
   * eg. "Double.compare(x, y);"
   * eg. "Objects.equals(x, y)"
   * eg. "x == y"
   */
  override fun renderWithoutDebugComments(config: RenderConfig) = when (config.targetLanguage) {
    C_17 -> TODO()
    CPP_14 -> TODO()
    CPP_17 -> TODO()
    CPP_20 -> TODO()
    DART_2 -> TODO()

    GOLANG_1_7 ->
      handleGolang(config)

    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> handleJava(config)

    KOTLIN_JVM_1_4,
    -> handleKotlin(config)

    else -> TODO()
  }

  /**
   * Builds java.lang.Object.equals based comparison expression
   * Useful in POJOs
   *
   * @return expression for java equality test (for 1 field)
   */
  @Suppress("ReturnCount")
  private fun handleJava(
    config: RenderConfig,
  ): String {
    val suffix = config.targetLanguage.statementTerminatorLiteral(config.terminate)
    val e0 = expression0.render(config.unterminated)
    val e1 = expression1.render(config.unterminated)

    if (expressionType.enumType ||
      expressionType.base == BOOLEAN ||
      expressionType.base == CHAR
    ) {
      return "$e0 == $e1$suffix"
    }

    // TODO: use formula like: abs(a/b - 1) < delta
    if (expressionType.base == FLOAT_64) {
      return "Double.compare($e0, $e1)$suffix"
    }

    if (expressionType.base == FLOAT_32) {
      return "Float.compare($e0, $e1)$suffix"
    }

    if (expressionType.base == ARRAY) {
      return "Arrays.deepEquals($e0, $e1)$suffix"
    }

    return "Objects.equals($e0, $e1)$suffix"
  }

  @Suppress("ReturnCount")
  private fun handleKotlin(
    config: RenderConfig,
  ): String {

    val e0 = expression0.render(config.unterminated)
    val e1 = expression1.render(config.unterminated)

    if (expressionType.base == FLOAT_64) {
      //TODO: use contentEquals
      return "java.lang.Double.compare($e0, $e1)"
    }

    if (expressionType.base == FLOAT_32) {
      //TODO: use contentEquals
      return "java.lang.Float.compare($e0, $e1)"
    }

    if (expressionType.base == ARRAY) {
      return "Arrays.deepEquals($e0, $e1)"
    }

    return "$e0 == $e1"
  }

  private fun handleGolang(
    config: RenderConfig,
  ): String {
    val e0 = expression0.render(config.unterminated)
    val e1 = expression1.render(config.unterminated)

    return "$e0 == $e1"
  }
}
