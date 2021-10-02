package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.Serde
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE

/**
 * Useful when converting from Proto generated class to POJO
 *
 * Wraps in serde
 */
class ProtobufFieldReadExpression(
  private val field: Field,

  private val assertNonNull: Boolean = false,

  private val fieldOwner: Expression = RawLiteralExpression("proto"),

  private val serde: Serde,

  private val protobufReadExpression: Expression = MethodInvokeExpression(
    arguments = listOf(),
    assertNonNull = assertNonNull,
    fieldOwner = fieldOwner,
    methodName = MethodNameExpression(
      name = field.protobufView.protobufGetterMethodName,
    )
  ),
) : Expression {

  override val expressionName: String = ProtobufFieldReadExpression::class.java.simpleName


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
  override fun renderWithoutDebugComments(config: RenderConfig): String {

    val serdeExpression = WrapWithSerdeExpression(
      serde = serde,
      serdeMode = DESERIALIZE,
      wrapped = protobufReadExpression,
    )

    return serdeExpression.render(config)
  }
}
