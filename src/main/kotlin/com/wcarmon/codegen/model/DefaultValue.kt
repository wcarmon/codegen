package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator


/**
 *  See Unit tests for more examples [DefaultValueTest]
 */
data class DefaultValue(
  private val emptyCollection: Boolean = false,
  private val presentAndNull: Boolean = false,
  private val quoteValue: Boolean = true,
  private val wrapper: ValueWrapper? = null,
) {

  // Must wrap the value to distinguish absence from null
  data class ValueWrapper(
    val value: Any?,
  )

  companion object {

    private val DEFAULT_NOT_PRESENT = DefaultValue(
      presentAndNull = false,
      quoteValue = false,
      wrapper = null,
    )

    private const val PROPERTY_NAME_FOR_QUOTING = "quoteValue"
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

      val quoteForStringField = jsonObj[PROPERTY_NAME_FOR_QUOTING] as? Boolean ?: true

      val wrapper =
        if (theDefault is List<*>) {
          check(theDefault.isEmpty())
          ValueWrapper(null)

        } else {
          ValueWrapper(theDefault)
        }

      return DefaultValue(
        emptyCollection = theDefault is List<*>,
        presentAndNull = theDefault == null,
        quoteValue = quoteForStringField,
        wrapper = wrapper,
      )
    }
  }

  /** isPresent, isNotAbsent, wasSetByUser, isProvided, hasDefault, hasValue */
  val isPresent: Boolean = wrapper != null

  val isAbsent: Boolean = wrapper == null

  fun isEmptyCollection(): Boolean = emptyCollection

  /**
   * Only readable when [isPresent]
   * User wants an explicit default to null/nil/NULL
   */
  fun isNullLiteral(): Boolean {
    check(isPresent) {
      "Only read when isPresent"
    }

    //TODO: quoting matters here
    return wrapper != null
        && wrapper.value == null
        && !emptyCollection
  }


  /**
   * WARNING: use with caution
   *
   * Callers are responsible for value interpretation
   */
  val uninterpreted: Any? = wrapper?.value

  val shouldQuote = quoteValue
}
