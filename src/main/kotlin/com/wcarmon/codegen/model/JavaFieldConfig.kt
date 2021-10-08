package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * [Field] attributes specific to a field on the JVM
 *
 * See src/main/resources/json-schema/jvm-field.schema.json
 */
@JsonPropertyOrder(alphabetic = true)
data class JavaFieldConfig(

  val defaultValue: DefaultValue = DefaultValue(),
  val overrideElasticSearchSerde: Serde? = null,
  val overrideKafkaSerde: Serde? = null,
  val overrideProtobufRepeatedItemSerde: Serde? = null,
  val overrideProtobufSerde: Serde? = null,
  val overrideRDBMSSerde: Serde? = null,

  @JsonProperty("validation")
  val validationConfig: FieldValidation = FieldValidation(),

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
)
