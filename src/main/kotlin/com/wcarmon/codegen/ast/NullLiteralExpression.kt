package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * c:           NULL
 * golang:      nil
 * groovy:      null
 * java:        null
 * javascript:  null
 * kotlin:      null
 * python:      None
 * RDBMS:       NULL
 * rust:        std::ptr::null    <--- TODO: verify this
 * typescript:  null
 */
object NullLiteralExpression : Expression {

  override val expressionName: String = NumericLiteralExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) = config.lineIndentation +
      when (config.targetLanguage) {
        GOLANG_1_9 -> "nil"
        PYTHON_3 -> "None"

        SQL_DB2,
        SQL_DELIGHT,
        SQL_H2,
        SQL_MARIA,
        SQL_MYSQL,
        SQL_ORACLE,
        SQL_POSTGRESQL,
        SQL_SQLITE,
        -> "NULL"

        else -> "null"
      } +
      config.statementTerminatorLiteral
}
