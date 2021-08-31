package com.wcarmon.codegen.model

import java.time.Instant
import kotlin.reflect.full.declaredMemberProperties

/**
 * Represents logical validations performed on [Field]
 *
 * See src/main/resources/json-schema/field-validation.schema.json
 *
 * assigning null disables a validation
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
  val requireNotBlank: Boolean? = null,
  val requireTrimmed: Boolean? = null,

  // TODO: consider combining these (eg. requireCase enum)
  val requireLowerCase: Boolean? = null,
  val requireUpperCase: Boolean? = null,


  // For Numeric
  val maxValue: Number? = null, // error when outside range
  val minValue: Number? = null, // error when outside range


  // For Temporal
  val after: Instant? = null,
  val before: Instant? = null,
) {

  val hasValidation: Boolean

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

    // null disables validation
    hasValidation = FieldValidation::class
      .declaredMemberProperties
      .any { property ->
        null != property.get(this)
      }
  }
}
