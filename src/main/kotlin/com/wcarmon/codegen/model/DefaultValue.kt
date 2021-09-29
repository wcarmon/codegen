package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.wcarmon.codegen.model.DefaultValue.Mode.*


/**
 *  See Unit tests for more examples [DefaultValueTest]
 */
data class DefaultValue(
  private val mode: Mode = ABSENT,

  private val wrapper: ValueWrapper? = null,
) {

  // Must wrap the value to distinguish absence from null
  data class ValueWrapper(
    val value: Any?,
  )

  enum class Mode {
    ABSENT,
    PRESENT__EMPTY_COLLECTION,
    PRESENT__NULL_LITERAL,
    PRESENT__OTHER,
  }

  companion object {

    private const val PROPERTY_NAME_FOR_QUOTING = "quoteValue"
    private const val PROPERTY_NAME_FOR_VALUE = "value"

    @JsonCreator
    @JvmStatic
    fun build(raw: Any?): DefaultValue {
      if (raw == null) {
        return DefaultValue(
          mode = ABSENT,
          wrapper = null,
        )
      }

      check(raw is Map<*, *>) {
        "Invalid defaultValue, must be an object or null.  " +
            "See DefaultValue documentation: value=$raw"
      }

      val jsonObj: Map<*, *> = raw

      val quoteValue = jsonObj[PROPERTY_NAME_FOR_QUOTING] as? Boolean ?: true

      if (jsonObj.isEmpty()) {
        return DefaultValue(
          mode = ABSENT,
          wrapper = null,
        )
      }

      if (!jsonObj.containsKey(PROPERTY_NAME_FOR_VALUE)) {
        return DefaultValue(
          mode = ABSENT,
          wrapper = null,
        )
      }

      // Invariant: default value present

      val theDefault: Any? = jsonObj[PROPERTY_NAME_FOR_VALUE]

      val emptyCollection =
        !quoteValue && theDefault is Collection<*> && theDefault.isEmpty() ||
            !quoteValue && theDefault is Map<*, *> && theDefault.isEmpty()


      val presentAndNull =
        if (quoteValue) {
          theDefault == null
        } else {
          theDefault == null ||
              setOf("null", "nil").contains(theDefault.toString().lowercase())
        }

      val mode =
        if (presentAndNull) {
          PRESENT__NULL_LITERAL

        } else if (emptyCollection) {
          PRESENT__EMPTY_COLLECTION

        } else {
          PRESENT__OTHER
        }

      val wrapper = when (mode) {
        ABSENT -> null

        PRESENT__EMPTY_COLLECTION,
        PRESENT__NULL_LITERAL,
        -> ValueWrapper(null)

        PRESENT__OTHER -> ValueWrapper(
          if (quoteValue) {
            "$theDefault"
          } else {
            theDefault
          }
        )
      }

      if (quoteValue == false && theDefault is CharSequence) {
        //TODO: log.warn
        println("Case is illogical: quoteValue==false and default is a string: '${theDefault}'")
      }

      return DefaultValue(
        mode = mode,
        wrapper = wrapper,
      )
    }
  }

  /** isPresent, isNotAbsent, wasSetByUser, isProvided, hasDefault, hasValue */
  val isPresent: Boolean = mode != ABSENT

  val isAbsent: Boolean = mode == ABSENT

  fun isEmptyCollection(): Boolean = mode == PRESENT__EMPTY_COLLECTION

  /**
   * Only readable when [isPresent]
   * User wants an explicit default to null/nil/NULL
   */
  fun isNullLiteral(): Boolean = mode == PRESENT__NULL_LITERAL

  /**
   * WARNING: use with caution
   *
   * Callers are responsible for value interpretation
   */
  val uninterpreted: Any? = wrapper?.value

  val shouldQuote: Boolean = wrapper?.value is CharSequence
}
