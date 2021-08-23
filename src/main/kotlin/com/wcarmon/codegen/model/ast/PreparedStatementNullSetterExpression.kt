package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*
import java.sql.JDBCType

/**
 * See [java.sql.PreparedStatement.setNull]
 * See [PreparedStatementNonNullSetterExpression]
 *
 * eg. ps.setNull(3, Types.INTEGER)
 *
 * <preparedStatementIdentifier>.setNull( columnIndex, <column-type> )
 *
 * Setting non-null values is handled by [PreparedStatementNonNullSetterExpression]
 */
data class PreparedStatementNullSetterExpression(
  val columnIndex: Int,
  val columnType: JDBCType,
  val preparedStatementIdentifier: String = "ps",
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
    -> handleJava(terminate)

    KOTLIN_JVM_1_4 -> handleKotlin()

    else -> TODO("Prepared statements not supported for targetLanguage=$targetLanguage")
  }

  private fun handleJava(terminate: Boolean) =
    "${getPreparedStatementPrefix()}setNull($columnIndex, Types.${columnType.name})" +
        serializeTerminator(terminate)

  private fun handleKotlin() = handleJava(false)

  private fun getPreparedStatementPrefix() =
    if (preparedStatementIdentifier.isBlank()) ""
    else "${preparedStatementIdentifier}."
}
