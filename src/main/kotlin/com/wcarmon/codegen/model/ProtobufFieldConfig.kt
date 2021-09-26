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
  val overrideTypeLiteral: String? = null,

  val repeated: Boolean = false,

//TODO: support oneOf

//TODO: support Maps: https://developers.google.com/protocol-buffers/docs/proto3#maps

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
) {

  init {
    require(overrideTypeLiteral == null || overrideTypeLiteral.isNotBlank()) {
      "overrideTypeLiteral must be non-blank or null"
    }
  }

  val overrideBaseType: BaseFieldType? =
    overrideTypeLiteral?.let { BaseFieldType.parse(it) }

  fun typeLiteral(defaultType: LogicalFieldType) =
    overrideTypeLiteral ?: protobufTypeLiteral(defaultType)
}