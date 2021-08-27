package com.wcarmon.codegen.model

/**
 * See https://developers.google.com/protocol-buffers/docs/proto3#assigning_field_numbers
 */
data class ProtoFieldNumber(
  val value: Int,
) {

  companion object {
    val FIRST: ProtoFieldNumber = ProtoFieldNumber(1)
  }

  init {
    require(value >= 1) {
      "Proto field numbers start at 1: fieldNumber=$value"
    }
  }
}
