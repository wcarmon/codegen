package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

//TODO: document me
@JsonPropertyOrder(alphabetic = true)
data class ProtocolBufferFieldConfig(

  val deprecated: Boolean = false,

  /**
   * A standard type or fully-qualified, user-defined message type
   * See https://developers.google.com/protocol-buffers/docs/proto3#scalar
   */
  val overrideTypeLiteral: String = "",

  val serde: Serde? = null,

  val repeated: Boolean = false,

  val repeatedItemSerde: Serde? = null,

//TODO: support oneOf

//TODO: support Maps: https://developers.google.com/protocol-buffers/docs/proto3#maps
) {
  init {
    if (repeatedItemSerde != null) {
      require(repeated) {
        "repeatedItemSerde is only for repeated fields (collections)"
      }
    }
  }
}
