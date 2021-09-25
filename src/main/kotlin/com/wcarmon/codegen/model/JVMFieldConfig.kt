package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * [Field] attributes specific to a field on the JVM
 *
 * See src/main/resources/json-schema/jvm-field.schema.json
 */
@JsonPropertyOrder(alphabetic = true)
data class JVMFieldConfig(

  val overrideSerde: Serde = Serde.INLINE,

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
)
