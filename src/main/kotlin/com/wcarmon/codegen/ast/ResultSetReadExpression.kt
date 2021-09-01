package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.QuoteType.DOUBLE


/**
 * See [java.sql.ResultSet]
 *
 * eg. rs.getString("foo")
 * eg. rs.getLong("bar")
 *
 * <resultSetIdentifier>.<getterName>(<fieldName>)
 */
class ResultSetReadExpression(
  fieldName: Name,
  getterMethod: Name,

  resultSetIdentifierExpression: Expression = RawLiteralExpression("rs"),
) : Expression {

  override val expressionName: String = ResultSetReadExpression::class.java.simpleName

  private val underlying: MethodInvokeExpression

  init {
    underlying = MethodInvokeExpression(
      // Single arg for column name
      arguments = listOf(
        StringLiteralExpression(
          quoteType = DOUBLE,
          value = fieldName.lowerSnake,
        )),
      assertNonNull = false,
      fieldOwner = resultSetIdentifierExpression,
      methodName = MethodNameExpression(getterMethod),
    )
  }

  override fun renderWithoutDebugComments(config: RenderConfig) = underlying.render(config)
}
