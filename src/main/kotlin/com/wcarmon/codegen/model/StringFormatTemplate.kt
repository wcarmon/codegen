package com.wcarmon.codegen.model

/**
 * Uses [String.format] to and a placeholder to expand the template
 */
data class StringFormatTemplate(

  /**
   * A template which can be formatted/expanded with [String.format]
   *
   * Should contain %s, but not required
   *
   * Counter examples: Method references
   */
  private val value: String,
) {

  companion object {

    /**
     * Inlines the placeholder
     */
    @JvmStatic
    val INLINE = StringFormatTemplate("%s")
  }

  init {
    require(value.isNotBlank()) {
      "value cannot be blank"
    }
  }

  fun expand(placeholder: String) =
    String.format(value, placeholder)
}
