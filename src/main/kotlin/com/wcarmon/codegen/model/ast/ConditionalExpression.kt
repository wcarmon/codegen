package com.wcarmon.codegen.model.ast

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

  override fun serialize(targetLanguage: TargetLanguage) = when (targetLanguage) {
    C_17,
    CPP_14,
    CPP_17,
    CPP_20,
    DART_2,
    JAVA_08,
    JAVA_11,
    JAVA_17,
    KOTLIN_JVM_1_4,
    TYPESCRIPT_4,
    -> cStyle(targetLanguage)

    GOLANG_1_7,
    RUST_1_54,
    SWIFT_5,
    -> noParens(targetLanguage)

    PYTHON_3 -> pythonStyle(targetLanguage)

    SQL -> TODO("Conditionals in SQL are not supported here")

//    else -> TODO("serialize: targetLanguage=$targetLanguage, this=$this")
  }

  private fun cStyle(targetLanguage: TargetLanguage) =
    if (expressionForFalse == null) {
      """
      |if (${condition.serialize(targetLanguage)}) {
      |  ${expressionForTrue.serialize(targetLanguage)}   
      |}
      """

    } else {
      """
      |if (${condition.serialize(targetLanguage)}) {
      |  ${expressionForTrue.serialize(targetLanguage)}   
      |} else {
      |  ${expressionForFalse.serialize(targetLanguage)}
      |}
      """
    }.trimMargin()

  private fun noParens(targetLanguage: TargetLanguage) =
    if (expressionForFalse == null) {
      """
      |if ${condition.serialize(targetLanguage)} {
      |  ${expressionForTrue.serialize(targetLanguage)}
      |}
      """
    } else {
      """
      |if ${condition.serialize(targetLanguage)} {
      |  ${expressionForTrue.serialize(targetLanguage)}
      |} else {
      |  ${expressionForFalse.serialize(targetLanguage)}
      |}
      """
    }.trimMargin()

  private fun pythonStyle(targetLanguage: TargetLanguage) =
    if (expressionForFalse == null) {
      """
      |if ${condition.serialize(targetLanguage)}:
      |  ${expressionForTrue.serialize(targetLanguage)}
      """
    } else {
      """
      |if ${condition.serialize(targetLanguage)}:
      |  ${expressionForTrue.serialize(targetLanguage)}
      |else:          
      |  ${expressionForFalse.serialize(targetLanguage)}
      """
    }.trimMargin()

}
