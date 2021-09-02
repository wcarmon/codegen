package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.JDBCColumnIndex
import com.wcarmon.codegen.model.Name
import java.sql.JDBCType

/**
 * For nullable fields:
 * - Combines [PreparedStatementSetNonNullExpression] and [PreparedStatementSetNullExpression]
 * - Wraps in a conditional
 *
 * For non-nullable fields
 * - Same as [PreparedStatementSetNonNullExpression]
 */
data class PreparedStatementSetExpression(
  private val columnIndex: JDBCColumnIndex,
  private val columnType: JDBCType,
  private val field: Field,
  private val fieldReadExpression: Expression,
  private val nullTestExpression: Expression,
  private val preparedStatementIdentifierExpression: Expression = RawLiteralExpression("ps"),
  private val setterMethod: Name,
) : Expression {

  override val expressionName: String = PreparedStatementSetExpression::class.java.simpleName

  private val expressionWhenNonNull: PreparedStatementSetNonNullExpression =
    PreparedStatementSetNonNullExpression(
      columnIndex = columnIndex,
      fieldReadExpression = fieldReadExpression,
      preparedStatementIdentifierExpression = preparedStatementIdentifierExpression,
      setterMethod = setterMethod,
    )

  private val expressionWhenNull: PreparedStatementSetNullExpression =
    PreparedStatementSetNullExpression(
      columnIndex = columnIndex,
      columnType = columnType,
      preparedStatementIdentifierExpression = preparedStatementIdentifierExpression,
    )

  private val conditionalExpression: ConditionalExpression =
    ConditionalExpression(
      condition = nullTestExpression,
      expressionForFalse = expressionWhenNonNull,
      expressionForTrue = expressionWhenNull,
    )

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) =
    if (field.type.nullable) {
      conditionalExpression

    } else {
      expressionWhenNonNull
    }
      .render(config.terminated)
}
