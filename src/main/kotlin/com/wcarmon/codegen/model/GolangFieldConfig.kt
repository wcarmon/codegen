package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * [Field] attributes specific to a field for golang
 *
 * See src/main/resources/json-schema/golang-field.schema.json
 */
@JsonPropertyOrder(alphabetic = true)
data class GolangFieldConfig(

  /**
   * Replace the auto-derived type with this literal
   */
  val overrideTypeLiteral: String? = null,

  val overrideElasticSearchSerde: Serde? = null,
  val overrideKafkaSerde: Serde? = null,
  val overrideProtobufRepeatedItemSerde: Serde? = null,
  val overrideProtobufSerde: Serde? = null,
  val overrideRDBMSSerde: Serde? = null,

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
) {

  val overrideBaseType: BaseFieldType? =
    overrideTypeLiteral?.let { BaseFieldType.parse(it) }

  init {
    require(overrideTypeLiteral == null || overrideTypeLiteral.isNotBlank()) {
      "overrideTypeLiteral must be null or non-blank"
    }
  }
}
