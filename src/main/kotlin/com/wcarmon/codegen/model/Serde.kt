package com.wcarmon.codegen.model

import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE

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
  val deserializeTemplate: StringFormatTemplate,

  /**
   * instance method or static method/function
   * Use %s as a placeholder for the field
   *
   * eg. "%s.toJsonString()"
   * eg. "objectWriter.writeValueAsString(%s)"
   *
   * No statement terminator required (no trailing semicolon)
   */
  val serializeTemplate: StringFormatTemplate,
) {

  companion object {

    /** Direct serialization & deserialization (no wrapper/parser methods)*/
    @JvmStatic
    val INLINE: Serde = Serde(
      StringFormatTemplate.INLINE,
      StringFormatTemplate.INLINE
    )
  }

  fun forMode(mode: SerdeMode) = when (mode) {
    SERIALIZE -> serializeTemplate
    DESERIALIZE -> deserializeTemplate
  }
}
