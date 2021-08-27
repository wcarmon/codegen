package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.QuoteType.DOUBLE
import com.wcarmon.codegen.model.TargetLanguage


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
  getterMethod: Name,   //TODO: use defaultResultSetGetterMethod(field.effectiveBaseType)
  resultSetIdentifierExpression: Expression? = RawExpression("rs"),
) : Expression {

  private val underlying: MethodInvokeExpression

  init {
    underlying = MethodInvokeExpression(
      // Single arg for column name
      arguments = listOf(
        StringLiteralExpression(
          quoteType = DOUBLE,
          fieldName.lowerSnake,
        )),
      assertNonNull = false,
      fieldOwner = resultSetIdentifierExpression,
      methodName = MethodNameExpression(getterMethod),
    )
  }

  override fun serialize(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) =
    underlying.serialize(targetLanguage, terminate)
}
