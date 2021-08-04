package com.wcarmon.codegen.model

import java.time.Instant

/**
 * Represents logical validations performed on [Field]
 *
 * See src/main/resources/json-schema/field-validation.schema.json
 */
data class FieldValidation(

  // For Files/Paths
  val fileConstraint: FileValidationConstraint? = null,


  // For Strings, Collections, ...
  val maxSize: Int? = null,
  val minSize: Int? = null,


  // For Collections
  //  val requireItemsSorted: Boolean = false, complicated: by what property? reversed?


  // For Strings
  val requireMatchesRegex: Regex? = null,
  val requireNotBlank: Boolean = false,
  val requireTrimmed: Boolean = false,

  // TODO: consider combining these (eg. requireCase enum)
  val requireLowerCase: Boolean = false,
  val requireUpperCase: Boolean = false,


  // For Numeric
  val coerceAtLeast: Number? = null, // coerce when outside range
  val coerceAtMost: Number? = null, // coerce when outside range
  val maxValue: Number? = null, // error when outside range
  val minValue: Number? = null, // error when outside range


  // For Temporal
  val after: Instant? = null,
  val before: Instant? = null,
) {
  init {

    if (maxSize != null && minSize != null) {
      require(maxSize >= minSize) {
        "Conflicting constraint: minSize=$minSize, maxSize=$maxSize"
      }
    }

    if (after != null && before != null) {
      require(after.isBefore(before)) {
        "Conflicting constraint: after=$after, before=$before"
      }
    }

    if (maxValue != null && minValue != null) {
      require(maxValue.toDouble() >= minValue.toDouble()) {
        "Conflicting constraint: maxValue=$maxValue, minValue=$minValue"
      }
    }

    //TODO: enforce no impossible combinations for coerceAtLeast & coerceAtMost
  }
}
