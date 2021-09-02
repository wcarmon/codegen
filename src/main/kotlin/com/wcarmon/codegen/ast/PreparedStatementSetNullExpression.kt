package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.NumericLiteralBase.DECIMAL
import com.wcarmon.codegen.model.JDBCColumnIndex
import com.wcarmon.codegen.model.Name
import java.sql.JDBCType

/**
 * See [java.sql.PreparedStatement.setNull]
 * See [PreparedStatementSetNonNullExpression]
 *
 * eg. ps.setNull(3, Types.INTEGER)
 *
 * <preparedStatementIdentifier>.setNull( columnIndex, <column-type> )
 *
 * Setting non-null values is handled by [PreparedStatementSetNonNullExpression]
 */
class PreparedStatementSetNullExpression(
  columnIndex: JDBCColumnIndex,
  columnType: JDBCType,
  preparedStatementIdentifierExpression: Expression = RawLiteralExpression("ps"),
) : Expression {

  override val expressionName: String = PreparedStatementSetNullExpression::class.java.simpleName

  private val underlying: MethodInvokeExpression

  init {
    underlying = MethodInvokeExpression(
      arguments = listOf(
        NumericLiteralExpression(DECIMAL, columnIndex.value),
        RawLiteralExpression("Types.${columnType.name}"),
      ),
      assertNonNull = false,
      fieldOwner = preparedStatementIdentifierExpression,
      methodName = MethodNameExpression(Name("setNull")),
    )
  }

  override fun renderWithoutDebugComments(config: RenderConfig) =
    underlying.render(config)
}
