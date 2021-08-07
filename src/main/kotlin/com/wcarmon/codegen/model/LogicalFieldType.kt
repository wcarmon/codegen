package com.wcarmon.codegen.model

/**
 * Represents all the aspects of a field's type (in popular languages)
 *
 * [BaseFieldType] handles most of the logic for predefined types
 * Generics are handled by [typeParameters] (parametric polymorphism)
 *
 * See language & framework specific extensions in [com.wcarmon.codegen.model.extensions.*]
 */
data class LogicalFieldType(
  val base: BaseFieldType,
  val nullable: Boolean = false,

  // -- Only numeric types
  val precision: Int = 0, // total # significant digits (both sides of decimal point)
  val scale: Int = 0,     // # decimal digits
  val signed: Boolean = true,

  /** Is this type limited to a bounded set of values? */
  val enumType: Boolean = false,

  /** Useful for user-defined types, fully qualified */
  val rawTypeLiteral: String,

  /**
   * fully qualified static function/method
   * Use %s as a placeholder for the serialized string
   *
   * eg. "com.foo.MyType.fromString(%s)"
   *
   * No statement terminator required (no trailing semicolon)
   */
  val jvmDeserializerTemplate: String = "",

  /**
   * instance method or static method/function
   * Use %s as a placeholder for the field
   *
   * eg. "%s.toJsonString()"
   *
   * No statement terminator required (no trailing semicolon)
   */
  val jvmSerializerTemplate: String = "",

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
) {

  init {
    require(precision <= 1_000) { "precision too high: $precision, field=$this" }
    require(precision >= 0) { "precision too low: $precision, field=$this" }

    require(scale <= precision) { "Scale too high: scale=$scale, precision=$precision, field=$this" }
    require(scale >= 0) { "Scale too low: $scale" }

    if (base.requiresPrecision()) {
      require(precision > 0) { "Precision too low: $this" }
    } else {
      require(precision == 0) { "Only numeric types can have precision: $this" }
    }

    if (!base.canHaveScale()) {
      //TODO: missing context
      require(scale == 0) { "field cannot have scale: $this" }
    }

    // -- Serde
    if (jvmDeserializerTemplate.isNotBlank()) {
      require(jvmSerializerTemplate.isNotBlank()) {
        "jvmSerializer required (to match jvmDeserializer): $this"
      }

      require(jvmDeserializerTemplate.contains("%s")) {
        "jvmSerializerTemplate must contain a placeholder for the serialized string"
      }
    }

    if (jvmSerializerTemplate.isNotBlank()) {
      require(jvmDeserializerTemplate.isNotBlank()) {
        "jvmDeserializer required (to match jvmSerializer): $this"
      }

      require(jvmSerializerTemplate.contains("%s")) {
        "jvmSerializerTemplate must contain a placeholder for the field"
      }
    }

    require(jvmSerializerTemplate.trim() == jvmSerializerTemplate) {
      "jvmSerializerTemplate must be trimmed: $this"
    }

    require(jvmDeserializerTemplate.trim() == jvmDeserializerTemplate) {
      "jvmDeserializerTemplate must be trimmed: $this"
    }

    // -- Parametric polymorphism
    val n = base.requiredTypeParameterCount()
    when (n) {
      //TODO: missing context
      0 -> require(typeParameters.isEmpty()) {
        "type parameter not allowed"
      }

      1 -> require(typeParameters.size == n) {
        "exactly 1-type parameter required (add 'typeParameters' to Field)"
      }

      else -> require(typeParameters.size == n) {
        "type parameters required (add 'typeParameters' to Field): requiredCount=$n, actualCount=${typeParameters.size}"
      }
    }
  }
}
