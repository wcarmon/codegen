package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.wcarmon.codegen.log.structuredWarn
import com.wcarmon.codegen.model.DefaultValue.Mode.*
import org.apache.logging.log4j.LogManager


/**
 *  See Unit tests for more examples [DefaultValueTest]
 */
data class DefaultValue(
  private val mode: Mode = ABSENT,

  private val allowQuoting: Boolean = true,
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

    private const val QUOTING_PROPERTY_NAME = "quoteValue"
    private const val VALUE_PROPERTY_NAME = "value"

    @JvmStatic
    private val LOG = LogManager.getLogger(DefaultValue::class.java)

    //TODO: attempt to simplify this
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

      val defaultValueJsonObject: Map<*, *> = raw

      if (defaultValueJsonObject.isEmpty()) {
        return DefaultValue(
          mode = ABSENT,
          wrapper = null,
        )
      }

      if (!defaultValueJsonObject.containsKey(VALUE_PROPERTY_NAME)) {
        return DefaultValue(
          mode = ABSENT,
          wrapper = null,
        )
      }

      // Invariant: value present

      val theDefault: Any? = defaultValueJsonObject[VALUE_PROPERTY_NAME]

      // For null/nil we require an explicit quoteValue==false
      val nullLiteral =
        if (defaultValueJsonObject[QUOTING_PROPERTY_NAME] as? Boolean != false) {
          theDefault == null

        } else {
          // When quoteValue==false, we treat some strings as null
          theDefault == null ||
              setOf("null", "nil")
                .contains(theDefault.toString().lowercase())
        }

      val quoteValue = defaultValueJsonObject[QUOTING_PROPERTY_NAME] as? Boolean ?: false

      val emptyCollection =
        !quoteValue && theDefault is Collection<*> && theDefault.isEmpty() ||
            !quoteValue && theDefault is Map<*, *> && theDefault.isEmpty()

      val mode =
        if (nullLiteral) {
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
            if (theDefault == "true" || theDefault == "false") {

              LOG.structuredWarn(
                "JSON config error: unnecessary quotes on boolean",
                "mode" to mode,
                "quoteValue" to quoteValue,
                "raw" to raw,
                "theDefault" to theDefault,
                "theDefault::class" to (theDefault.javaClass.name ?: "<null>"),
              )
            }

            //TODO: warn about unnecessary quotes on numbers

            theDefault
          }
        )
      }

      val allowQuoting =
        defaultValueJsonObject[QUOTING_PROPERTY_NAME] as? Boolean ?: true

      if (!allowQuoting
        && mode == PRESENT__OTHER
        && isBlankCharSequence(theDefault)
      ) {
        LOG.structuredWarn(
          "JSON config error: blank strings must be quoted",
          "allowQuoting" to allowQuoting,
          "emptyCollection" to emptyCollection,
          "mode" to mode,
          "quoteValue" to quoteValue,
          "raw" to raw,
          "theDefault" to theDefault,
          "theDefault::class" to (theDefault?.javaClass?.name ?: "<null>"),
        )
      }

      return DefaultValue(
        allowQuoting = allowQuoting || isBlankCharSequence(theDefault),
        mode = mode,
        wrapper = wrapper,
      )
    }

    private fun isBlankCharSequence(o: Any?): Boolean = o is CharSequence && o.isBlank()
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

  val shouldQuote: Boolean = wrapper?.value is CharSequence && allowQuoting
}
