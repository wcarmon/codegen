package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.NumericLiteralBase.DECIMAL
import com.wcarmon.codegen.model.JDBCColumnIndex
import com.wcarmon.codegen.model.Name
import java.sql.JDBCType

/**
 * See [java.sql.PreparedStatement.setNull]
 * See [PreparedStatementNonNullSetExpression]
 *
 * eg. ps.setNull(3, Types.INTEGER)
 *
 * <preparedStatementIdentifier>.setNull( columnIndex, <column-type> )
 *
 * Setting non-null values is handled by [PreparedStatementNonNullSetExpression]
 */
class PreparedStatementNullSetExpression(
  columnIndex: JDBCColumnIndex,
  columnType: JDBCType,
  preparedStatementIdentifierExpression: Expression = RawExpression("ps"),
) : Expression {

  override val expressionName = PreparedStatementNullSetExpression::class.java.name

  private val underlying: MethodInvokeExpression

  init {
    underlying = MethodInvokeExpression(
      arguments = listOf(
        NumericLiteralExpression(DECIMAL, columnIndex.value),
        RawExpression("Types.${columnType.name}"),
      ),
      assertNonNull = false,
      fieldOwner = preparedStatementIdentifierExpression,
      methodName = MethodNameExpression(Name("setNull")),
    )
  }

  override fun render(config: RenderConfig) = underlying.render(config)
}
