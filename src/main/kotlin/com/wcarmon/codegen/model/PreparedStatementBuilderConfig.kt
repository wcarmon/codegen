package com.wcarmon.codegen.model

/**
 * Configurable properties for generating PreparedStatement setters
 * See [java.sql.PreparedStatement]
 */
data class PreparedStatementBuilderConfig(

  /**
   * Generally sensible only as a JVM language (eg. Kotlin, Java, ...)
   */
  val targetLanguage: TargetLanguage,

  /**
   * Only for languages where compiler checks for null
   */
  val allowFieldNonNullAssertion: Boolean = true,

  /**
   * scope for reading field.  eg. "myEntity."
   */
  val fieldReadPrefix: String = "",

  val fieldReadStyle: FieldReadStyle = targetLanguage.fieldReadStyle,

  /**
   * Prefix for invoking a [java.sql.PreparedStatement] setter method
   */
  val preparedStatementIdentifier: String = "ps",
) {
  init {
    require(fieldReadPrefix.trim() == fieldReadPrefix) {
      "fieldReadPrefix must be trimmed: $fieldReadPrefix"
    }

    require(preparedStatementIdentifier.trim() == preparedStatementIdentifier) {
      "preparedStatementIdentifier must be trimmed: $preparedStatementIdentifier"
    }

    //TODO: be more strict via regex for preparedStatementIdentifier
  }
}
