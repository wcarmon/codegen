package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * See [java.sql.ResultSet]
 *
 * eg. rs.getString("foo")
 * eg. rs.getLong("bar")
 *
 * <resultSetIdentifier>.<getterName>(<fieldName>)
 */
data class ResultSetGetterExpression(
  val fieldName: Name,
  val getterMethod: MethodName,
  val resultSetIdentifier: String = "rs", //TODO: make a type for this
) : Expression {
  init {

    require(!resultSetIdentifier.endsWith(".")) {
      "resultSetIdentifier must not end with dot/period: value=$resultSetIdentifier"
    }

    //TODO: check for valid identifier (across languages) on resultSetIdentifier
  }

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) = when (targetLanguage) {
    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> handleJava(terminate)

    KOTLIN_JVM_1_4 -> handleKotlin()

    else -> TODO("ResultSet not supported for targetLanguage=$targetLanguage")
  }

  private fun handleJava(terminate: Boolean) =
    """${getResultSetPrefix()}$getterMethod("${fieldName.lowerSnake}")""" +
        serializeTerminator(terminate)

  private fun handleKotlin() = handleJava(false)

  private fun getResultSetPrefix() =
    if (resultSetIdentifier.isBlank()) ""
    else "${resultSetIdentifier}."

}
