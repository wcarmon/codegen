package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.effectiveProtoSerde
import com.wcarmon.codegen.util.protoBuilderGetter

/**
 * Useful when converting from Proto generated class to POJO
 */
class ProtoFieldReadExpression(
  private val assertNonNull: Boolean,
  private val field: Field,
  private val fieldOwner: Expression = DEFAULT_FIELD_OWNER,
  private val protoReadExpression: Expression,
) : Expression {

  companion object {

    private val DEFAULT_FIELD_OWNER = RawExpression("proto")

    @JvmStatic
    fun build(
      assertNonNull: Boolean = false,
      field: Field,
      fieldOwner: Expression = DEFAULT_FIELD_OWNER,
    ) = ProtoFieldReadExpression(
      field = field,
      assertNonNull = assertNonNull,
      protoReadExpression = MethodInvokeExpression(
        arguments = listOf(),
        assertNonNull = assertNonNull,
        fieldOwner = fieldOwner,
        methodName = MethodNameExpression(
          name = protoBuilderGetter(field)
        )
      )
    )
  }


  /**
   * eg. myCollectionDeserializer(proto.getAllFoo())
   * eg. myCollectionDeserializer(proto.getAllFoo());
   * eg. myDeserializer(proto.foo!!)
   * eg. myDeserializer(proto.foo)
   * eg. myDeserializer(proto.getFoo());
   * eg. proto.foo
   * eg. proto.foo!!
   * eg. proto.getFoo()
   */
  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ): String {

    val serdeExpression = WrapWithSerdeExpression(
      serde = effectiveProtoSerde(field),
      serdeMode = DESERIALIZE,
      wrapped = protoReadExpression,
    )

    return serdeExpression.render(targetLanguage, terminate)
  }
}
