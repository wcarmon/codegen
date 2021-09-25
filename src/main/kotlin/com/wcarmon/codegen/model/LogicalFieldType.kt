package com.wcarmon.codegen.model

import com.wcarmon.codegen.model.BaseFieldType.*

const val MAX_PRECISION = 1_000

/**
 * Represents all the aspects of a field's type (in popular languages)
 *
 * [BaseFieldType] handles most of the logic for predefined types
 * Generics are handled by [typeParameters] (parametric polymorphism)
 *
 * Also applies to variables & parameters
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
) {

  init {
    if (base.requiresPrecision) {
      requireNotNull(precision) { "precision is required for type=$this" }
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
  }
}
