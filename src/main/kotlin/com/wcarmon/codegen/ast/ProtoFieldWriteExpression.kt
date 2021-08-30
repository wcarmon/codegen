package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage
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

  /**
   * eg. addAllFoo( myCollectionSerializer(entity.foo) )
   * eg. addAllFoo( myCollectionSerializer(entity.getFoo()) )
   * eg. setFoo( entity.foo!! )
   * eg. setFoo( entity.getFoo() );
   * eg. setFoo( entity.getFoo().toString() );
   * eg. setFoo( mySerializer(entity.foo) )
   */
  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ): String {
    val setterName = protoBuilderSetter(field)
    val suffix = targetLanguage.statementTerminatorLiteral(terminate)
    return "${setterName}(${sourceReadExpression.render(targetLanguage, false)})${suffix}"
  }
}
