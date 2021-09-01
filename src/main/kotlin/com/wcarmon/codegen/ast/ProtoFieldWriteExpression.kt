package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.util.protoBuilderSetter

/**
 * Useful when converting from POJO to Proto generated class (via builder)
 */
data class ProtoFieldWriteExpression(
  private val field: Field,

  /**
   * typically a [FieldReadExpression] or [WrapWithSerdeExpression]
   */
  private val sourceReadExpression: Expression,
) : Expression {

  override val expressionName: String = ProtoFieldWriteExpression::class.java.simpleName

  /**
   * eg. addAllFoo( myCollectionSerializer(entity.foo) )
   * eg. addAllFoo( myCollectionSerializer(entity.getFoo()) )
   * eg. setFoo( entity.foo!! )
   * eg. setFoo( entity.getFoo() );
   * eg. setFoo( entity.getFoo().toString() );
   * eg. setFoo( mySerializer(entity.foo) )
   */
  override fun renderWithoutDebugComments(config: RenderConfig) =
    config.lineIndentation +
        protoBuilderSetter(field) +
        "(" +
        sourceReadExpression.render(config.unindented.unterminated) +
        ")" +
        config.statementTerminatorLiteral
}
