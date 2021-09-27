package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator


/**
 * In JSON config...
 *
 * No default (Absent):
 *   1. (omit Field.defaultValue)
 *   2. ..., "default": null, ...
 *   3. ..., "default": {}, ...
 *
 * Default to null:
 *   1. ..., "defaultValue": {"value": null}, ...
 *
 * Non-null default value:
 *   1. ..., "default": {"value": "foo"}, ...
 *   2. ..., "default": {"value": 3.14}, ...
 *   3. ..., "default": {"value": 7}, ...
 *   4. ..., "default": {"value": false}, ...
 *   5. ..., "default": {"value": true}, ...
 *   6. ..., "default": {"value": ""}, ...
 *
 *  See tests for more examples [DefaultValueTest]
 */
data class DefaultValue(
  private val wrapper: ValueWrapper? = null,

  private val emptyCollection: Boolean = false,

  private val presentAndNull: Boolean = false,
) {

  // Must wrap the value to distinguish absence from null
  data class ValueWrapper(
    val value: Any?,
  )

  companion object {

    private val DEFAULT_NOT_PRESENT = DefaultValue(wrapper = null, presentAndNull = false)

    private const val PROPERTY_NAME_FOR_VALUE = "value"

    @JsonCreator
    @JvmStatic
    fun build(raw: Any?): DefaultValue {
      if (raw == null) {
        return DEFAULT_NOT_PRESENT
      }

      check(raw is Map<*, *>) {
        "Invalid defaultValue, must be an object or null.  " +
            "See DefaultValue documentation: value=$raw"
      }

      val jsonObj: Map<*, *> = raw
      if (jsonObj.isEmpty()) {
        return DEFAULT_NOT_PRESENT
      }

      //Invariant: default value present

      val theDefault: Any? = jsonObj[PROPERTY_NAME_FOR_VALUE]

      val wrapper =
        if (theDefault is Collection<*>) {
          check(theDefault.isEmpty())
          ValueWrapper(null)

        } else {
          ValueWrapper(theDefault)
        }

      return DefaultValue(
        emptyCollection = theDefault is Collection<*>,
        presentAndNull = theDefault == null,
        wrapper = wrapper,
      )
    }
  }

  /** isPresent, isNotAbsent, wasSetByUser, isProvided, hasDefault, hasValue */
  val isPresent: Boolean = wrapper != null

  val isAbsent: Boolean = wrapper == null

  /**
   * Only readable when [isPresent]
   * User wants an explicit default to null/nil/NULL
   */
  val isNullLiteral: Boolean by lazy {
    check(isPresent) {
      "only read when hasValue==true"
    }

    wrapper != null
        && wrapper.value == null
        && !emptyCollection
  }

  val isEmptyString: Boolean by lazy {
    check(isPresent) {
      "only read when hasValue==true"
    }

    wrapper?.value != null &&
        wrapper.value is String &&
        wrapper.value.isEmpty()
  }

  val isBlankString: Boolean by lazy {
    check(isPresent) {
      "only read when hasValue==true"
    }

    wrapper?.value != null &&
        wrapper.value is String &&
        wrapper.value.isBlank()
  }

  val isEmptyCollection: Boolean by lazy {
    emptyCollection
  }

  /** Only readable when [isPresent] */
  val literal: Any? by lazy {
    check(wrapper != null) {
      "defaultValue only readable when present (check with field.defaultValue.isPresent)"
    }

    wrapper.value
  }
}
