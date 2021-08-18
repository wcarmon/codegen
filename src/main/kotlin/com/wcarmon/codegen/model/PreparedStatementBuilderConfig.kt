package com.wcarmon.codegen.model

//TODO: document me
data class PreparedStatementBuilderConfig(
  val targetLanguage: TargetLanguage,

  val allowFieldNonNullAssertion: Boolean = true,
  val fieldReadPrefix: String = "",
  val fieldReadStyle: FieldReadStyle = targetLanguage.fieldReadStyle,
  val preparedStatementIdentifier: String = "ps",
) {
  init {
    require(fieldReadPrefix.trim() == fieldReadPrefix) {
      "fieldReadPrefix must be trimmed: $fieldReadPrefix"
    }
  }
}
