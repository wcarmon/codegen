package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field

/**
 * Useful when converting from POJO to generated proto class (via builder)
 *
 * See examples below
 */
data class ProtobufFieldWriteExpression(
  private val field: Field,

  /**
   * typically a [FieldReadExpression] or [WrapWithSerdeExpression]
   */
  private val sourceReadExpression: Expression,
) : Expression {

  override val expressionName: String = ProtobufFieldWriteExpression::class.java.simpleName

  /**
   * eg. addAllFoo( myCollectionSerializer(entity.foo) )
   * eg. addAllFoo( myCollectionSerializer(entity.getFoo()) )
   * eg. setFoo( entity.foo!! )
   * eg. setFoo( entity.getFoo() );
   * eg. setFoo( entity.getFoo() == null ? "" : entity.getFoo() );  // java
   * eg. setFoo( entity.getFoo() ?: "" )  // kotlin
   * eg. setFoo( entity.getFoo().toString() );
   * eg. setFoo( mySerializer(entity.foo) )
   */
  override fun renderWithoutDebugComments(config: RenderConfig) =
    config.lineIndentation +
        "." +
        field.protobufView.protobufSetterMethodName.lowerCamel +
        "(" +
        sourceReadExpression.render(config.unindented.unterminated) +
        ")" +
        config.statementTerminatorLiteral
}
