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
  val overrideTypeLiteral: String = "",

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
) {

  val overrideBaseType: BaseFieldType? =
    if (overrideTypeLiteral.isNotBlank()) {
      BaseFieldType.parse(overrideTypeLiteral)
    } else {
      null
    }
}
