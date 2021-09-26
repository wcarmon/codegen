package com.wcarmon.codegen.model

/**
 * See https://developers.google.com/protocol-buffers/docs/proto3#assigning_field_numbers
 */
data class ProtobufFieldNumber(
  val value: Int,
) : Comparable<ProtobufFieldNumber> {

  companion object {
    val FIRST: ProtobufFieldNumber = ProtobufFieldNumber(1)
  }

  init {
    require(value >= 1) {
      "Protobuf field numbers start at 1: fieldNumber=$value"
    }
  }

  override fun compareTo(other: ProtobufFieldNumber) = this.value.compareTo(other.value)
}
