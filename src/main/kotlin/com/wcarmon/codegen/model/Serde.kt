package com.wcarmon.codegen.model

/**
 * Corresponding pair of serializer/marshaller & deserializer/unmarshaller
 */
data class Serde(

  /**
   * fully qualified static function/method
   * Use %s as a placeholder for the serialized value
   *
   * eg. "com.foo.MyType.fromString(%s)"
   * eg. "com.foo.MyType.fromDBString(%s)"
   *
   * No statement terminator required (no trailing semicolon)
   */
  val deserialize: ExpressionTemplate,

  /**
   * instance method or static method/function
   * Use %s as a placeholder for the field
   *
   * eg. "%s.toJsonString()"
   *
   * No statement terminator required (no trailing semicolon)
   */
  val serialize: ExpressionTemplate,
) {

  companion object {

    @JvmStatic
    val INLINE: Serde = Serde(
      ExpressionTemplate.INLINE,
      ExpressionTemplate.INLINE)
  }
}
