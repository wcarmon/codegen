package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * See [java.sql.PreparedStatement]
 * See [PreparedStatementNullSetterExpression]
 *
 * eg. ps.setString(7, myEntity.getFoo())
 * eg. ps.setDouble(8, entity.bar)
 *
 * <preparedStatementIdentifier>.<setterName>( columnIndex, newValueExpression )
 */
data class PreparedStatementNonNullSetterExpression(
  val columnIndex: Int,
  val newValueExpression: Expression,
  val preparedStatementIdentifier: String = "ps", //TODO: make a type for this
  val setter: MethodName,
) : Expression {

  init {
    require(columnIndex >= 1) {
      "columnIndex starts at 1: columnIndex=$columnIndex"
    }

    require(!preparedStatementIdentifier.endsWith(".")) {
      "preparedStatementIdentifier must not end with dot/period: value=$preparedStatementIdentifier"
    }

    //TODO: check for valid identifier (across languages) on preparedStatementIdentifier
  }

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = when (targetLanguage) {

    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> handleJava(targetLanguage, terminate)

    KOTLIN_JVM_1_4 -> handleKotlin(targetLanguage)

    else -> TODO("Prepared statements not supported for targetLanguage=$targetLanguage")
  }

  private fun handleJava(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) =
    "${getPreparedStatementPrefix()}$setter($columnIndex, ${
      newValueExpression.serialize(targetLanguage, false)
    })" + serializeTerminator(terminate)

  private fun handleKotlin(targetLanguage: TargetLanguage) =
    handleJava(targetLanguage, false)

  private fun getPreparedStatementPrefix() =
    if (preparedStatementIdentifier.isBlank()) ""
    else "${preparedStatementIdentifier}."
}
