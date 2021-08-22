package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

//TODO: document me
@JsonPropertyOrder(alphabetic = true)
data class ProtocolBufferFieldConfig(

  /**
   * A standard type or fully-qualified, user-defined message type
   */
  val overrideType: String = "",
)
