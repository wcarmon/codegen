package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.util.protobufTypeLiteral

//TODO: document me
@JsonPropertyOrder(alphabetic = true)
data class ProtobufFieldConfig(

  val deprecated: Boolean = false,

  /**
   * A standard type or fully-qualified, user-defined message type
   * See https://developers.google.com/protocol-buffers/docs/proto3#scalar
   */
  val overrideEffectiveType: String = "",

  val repeated: Boolean = false,

//TODO: support oneOf

//TODO: support Maps: https://developers.google.com/protocol-buffers/docs/proto3#maps

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
) {

  val overrideBaseType: BaseFieldType? =
    if (overrideEffectiveType.isNotBlank()) {
      BaseFieldType.parse(overrideEffectiveType)
    } else {
      null
    }

  fun typeLiteral(defaultType: LogicalFieldType) =
    if (overrideEffectiveType.isNotBlank()) {
      overrideEffectiveType
    } else {
      protobufTypeLiteral(defaultType)
    }
}
