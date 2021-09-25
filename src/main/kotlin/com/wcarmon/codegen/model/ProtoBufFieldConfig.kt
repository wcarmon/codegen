package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.util.protobufTypeLiteral

//TODO: document me
@JsonPropertyOrder(alphabetic = true)
data class ProtoBufFieldConfig(

  val deprecated: Boolean = false,

  /**
   * A standard type or fully-qualified, user-defined message type
   * See https://developers.google.com/protocol-buffers/docs/proto3#scalar
   */
  val overrideTypeLiteral: String? = null,

  val overrideSerde: Serde? = null,

  val repeated: Boolean = false,

  /**
   * Only used for repeated fields
   */
  val overrideRepeatedItemSerde: Serde? = null,

//TODO: support oneOf

//TODO: support Maps: https://developers.google.com/protocol-buffers/docs/proto3#maps

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
) {

  init {
    if (overrideTypeLiteral != null) {
      require(overrideTypeLiteral.isNotBlank()) { "overrideTypeLiteral must be non-blank or null" }
    }
  }

  val overrideBaseType: BaseFieldType? =
    overrideTypeLiteral?.let { BaseFieldType.parse(it) }

  fun typeLiteral(defaultType: LogicalFieldType) =
    overrideTypeLiteral ?: protobufTypeLiteral(defaultType)
}
