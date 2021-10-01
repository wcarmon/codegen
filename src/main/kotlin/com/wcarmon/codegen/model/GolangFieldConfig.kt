package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * [Field] attributes specific to a field for golang
 *
 * See src/main/resources/json-schema/golang-field.schema.json
 */
@JsonPropertyOrder(alphabetic = true)
data class GolangFieldConfig(

  val defaultValue: DefaultValue = DefaultValue(),

  /**
   * Replace the auto-derived type with this literal
   */
  val overrideEffectiveType: String? = null,

  val overrideElasticSearchSerde: Serde? = null,
  val overrideKafkaSerde: Serde? = null,
  val overrideProtobufRepeatedItemSerde: Serde? = null,
  val overrideProtobufSerde: Serde? = null,
  val overrideRDBMSSerde: Serde? = null,

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
) {

  val overrideBaseType: BaseFieldType? =
    overrideEffectiveType?.let { BaseFieldType.parse(it) }

  init {
    require(overrideEffectiveType == null || overrideEffectiveType.isNotBlank()) {
      "overrideEffectiveType must be null or non-blank"
    }
  }
}
