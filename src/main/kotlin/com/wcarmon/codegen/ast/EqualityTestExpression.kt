package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.TargetLanguage
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

  /**
   * eg. "Arrays.deepEquals(xArr, yArr)"
   * eg. "Arrays.deepEquals(xArr, yArr);"
   * eg. "Double.compare(x, y)"
   * eg. "Double.compare(x, y);"
   * eg. "Objects.equals(x, y)"
   * eg. "x == y"
   */
  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = when (targetLanguage) {
    C_17 -> TODO()
    CPP_14 -> TODO()
    CPP_17 -> TODO()
    CPP_20 -> TODO()
    DART_2 -> TODO()

    GOLANG_1_7 ->
      handleGolang(targetLanguage)

    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> handleJava(targetLanguage, terminate)

    KOTLIN_JVM_1_4,
    -> handleKotlin(targetLanguage)

    PROTOCOL_BUFFERS_3 -> TODO()
    PYTHON_3 -> TODO()
    RUST_1_54 -> TODO()

    SQL_DB2 -> TODO()
    SQL_H2 -> TODO()
    SQL_MARIA -> TODO()
    SQL_MYSQL -> TODO()
    SQL_ORACLE -> TODO()
    SQL_POSTGRESQL -> TODO()
    SQL_SQLITE -> TODO()

    SWIFT_5 -> TODO()

    TYPESCRIPT_4 -> TODO()
  }

  @Suppress("ReturnCount")
  private fun handleKotlin(
    targetLanguage: TargetLanguage,
  ): String {

    val e0 = expression0.render(targetLanguage, false)
    val e1 = expression1.render(targetLanguage, false)

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

  /**
   * Builds java.lang.Object.equals based comparison expression
   * Useful in POJOs
   *
   * @return expression for java equality test (for 1 field)
   */
  @Suppress("ReturnCount")
  private fun handleJava(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ): String {
    val suffix = targetLanguage.statementTerminatorLiteral(terminate)
    val e0 = expression0.render(targetLanguage, false)
    val e1 = expression1.render(targetLanguage, false)

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

  private fun handleGolang(
    targetLanguage: TargetLanguage,
  ): String {
    val e0 = expression0.render(targetLanguage, false)
    val e1 = expression1.render(targetLanguage, false)

    return "$e0 == $e1"
  }
}
