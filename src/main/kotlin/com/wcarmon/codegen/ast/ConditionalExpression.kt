package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * If or If-Else expression
 *
 * For Java: a Conditional Statement
 * For Kotlin: a Conditional Expression
 */
data class ConditionalExpression(
  val condition: Expression,
  val expressionForTrue: Expression,
  val expressionForFalse: Expression? = null,
) : Expression {

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = when (targetLanguage) {
    C_17,
    CPP_14,
    CPP_17,
    CPP_20,
    DART_2,
    JAVA_08,
    JAVA_11,
    JAVA_17,
    TYPESCRIPT_4,
    -> cStyle(targetLanguage, terminate)

    KOTLIN_JVM_1_4,
    -> cStyle(targetLanguage, false)

    GOLANG_1_7,
    RUST_1_54,
    SWIFT_5,
    -> noParens(targetLanguage, false)

    PYTHON_3 -> pythonStyle(targetLanguage)

    PROTOCOL_BUFFERS_3,
    -> TODO("Conditionals not supported on $targetLanguage")

    else -> TODO("Conditionals not supported on $targetLanguage")
  }

  private fun cStyle(
    targetLanguage: TargetLanguage,
    terminate: Boolean = false,
  ) =
    if (expressionForFalse == null) {
      """
      |if (${condition.serialize(targetLanguage, false)}) {
      |  ${expressionForTrue.serialize(targetLanguage, terminate)}   
      |}
      |
      """

    } else {
      """
      |if (${condition.serialize(targetLanguage, false)}) {
      |  ${expressionForTrue.serialize(targetLanguage, terminate)}   
      |} else {
      |  ${expressionForFalse.serialize(targetLanguage, terminate)}
      |}
      |
      """
    }.trimMargin()

  private fun noParens(
    targetLanguage: TargetLanguage,
    terminate: Boolean = false,
  ) =
    if (expressionForFalse == null) {
      """
      |if ${condition.serialize(targetLanguage, false)} {
      |  ${expressionForTrue.serialize(targetLanguage, terminate)}
      |}
      |
      """
    } else {
      """
      |if ${condition.serialize(targetLanguage, false)} {
      |  ${expressionForTrue.serialize(targetLanguage, terminate)}
      |} else {
      |  ${expressionForFalse.serialize(targetLanguage, terminate)}
      |}
      |
      """
    }.trimMargin()

  private fun pythonStyle(targetLanguage: TargetLanguage) =
    if (expressionForFalse == null) {
      """
      |if ${condition.serialize(targetLanguage, false)}:
      |  ${expressionForTrue.serialize(targetLanguage, false)}
      |
      """
    } else {
      """
      |if ${condition.serialize(targetLanguage, false)}:
      |  ${expressionForTrue.serialize(targetLanguage, false)}
      |else:          
      |  ${expressionForFalse.serialize(targetLanguage, false)}
      |
      """
    }.trimMargin()

}
