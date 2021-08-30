package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.NumericLiteralBase.DECIMAL
import com.wcarmon.codegen.model.JDBCColumnIndex
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage

/**
 * See [java.sql.PreparedStatement]
 * See [PreparedStatementNullSetExpression]
 *
 * eg. ps.setString(7, myEntity.getFoo())
 * eg. ps.setDouble(8, entity.bar)
 *
 * <preparedStatementIdentifier>.<setterName>( columnIndex, newValueExpression )
 *
 * setting null values are handled by [PreparedStatementNullSetExpression]
 */
class PreparedStatementNonNullSetExpression(
  columnIndex: JDBCColumnIndex,
  fieldReadExpression: Expression,
  setterMethod: Name,
  preparedStatementIdentifierExpression: Expression = RawExpression("ps"),
) : Expression {

  private val underlying: MethodInvokeExpression

  init {
    underlying = MethodInvokeExpression(
      arguments = listOf(
        NumericLiteralExpression(DECIMAL, columnIndex.value),
        fieldReadExpression,
      ),
      assertNonNull = false,
      fieldOwner = preparedStatementIdentifierExpression,
      methodName = MethodNameExpression(setterMethod),
    )
  }

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ) =
    underlying.render(
      targetLanguage,
      terminate,
      lineIndentation)
}
