package com.wcarmon.codegen.model

const val MAX_PRECISION = 1_000

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
  val precision: Int? = null, // total # significant digits (both sides of decimal point)
  val scale: Int = 0,     // # decimal digits
  val signed: Boolean = true,

  /** Is this type limited to a bounded set of values? */
  val enumType: Boolean = false,

  /**
   * Useful for user-defined types, fully qualified
   * Use syntax for java, kotlin, golang, rust, typescript, or postgres
   * */
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
    if (base.requiresPrecision) {
      requireNotNull(precision) { "precision is required" }
    }

    if (!base.canHavePrecision) {
      require(precision == null) { "Only numeric types can have precision: this=$this" }
    }

    if (precision != null) {
      require(precision <= MAX_PRECISION) {
        "precision too high: precision=$precision, this=$this"
      }

      require(precision > 0) {
        "precision must be positive: precision=$precision, this=$this"
      }

      require(scale <= precision) {
        "Scale too high: scale=$scale, precision=$precision, this=$this"
      }
    }

    if (!base.canHaveScale) {
      require(scale == 0) { "field cannot have scale: this=$this" }
    }

    require(scale >= 0) { "Scale must be non-negative: $scale, this=$this" }


    // -- Serde
    if (jvmDeserializerTemplate.isNotBlank()) {
      require(jvmSerializerTemplate.isNotBlank()) {
        "jvmSerializerTemplate required (to match jvmDeserializerTemplate): this=$this"
      }

      require(jvmDeserializerTemplate.contains("%s")) {
        "jvmDeserializerTemplate must contain a placeholder for the serialized string: this=$this"
      }
    }

    if (jvmSerializerTemplate.isNotBlank()) {
      require(jvmDeserializerTemplate.isNotBlank()) {
        "jvmDeserializerTemplate required (to match jvmSerializerTemplate): this=$this"
      }

      require(jvmSerializerTemplate.contains("%s")) {
        "jvmSerializerTemplate must contain a placeholder for the field: this=$this"
      }
    }

    require(jvmSerializerTemplate.trim() == jvmSerializerTemplate) {
      "jvmSerializerTemplate must be trimmed: this=$this"
    }

    require(jvmDeserializerTemplate.trim() == jvmDeserializerTemplate) {
      "jvmDeserializerTemplate must be trimmed: this=$this"
    }

    // -- Parametric polymorphism
    val n = base.requiredTypeParameterCount
    when (n) {
      //TODO: missing context
      0 -> require(typeParameters.isEmpty()) {
        "type parameter not allowed: this=$this"
      }

      1 -> require(typeParameters.size == n) {
        "exactly 1-type parameter required (add 'typeParameters' to Field): this=$this"
      }

      else -> require(typeParameters.size == n) {
        "type parameters required (add 'typeParameters' to Field): " +
            "requiredCount=$n, actualCount=${typeParameters.size}, this=$this"
      }
    }
  }
}
