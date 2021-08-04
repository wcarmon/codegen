package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.google.common.base.CaseFormat
import com.google.common.base.CaseFormat.*
import com.wcarmon.codegen.MAX_NAME_LENGTH

/**
 * Identifier for Entity or Field
 */
data class Name(
  // Either upper camel or lower camel
  private val camelCase: String,
) {

  @JsonValue
  val lowerCamel: String
  val lowerKebab: String
  val lowerSnake: String
  val upperCamel: String
  val upperSnake: String

  companion object {
    @JvmStatic
    @JsonCreator
    fun build(camelCase: String) = Name(camelCase)
  }

  init {
    require(camelCase.isNotBlank()) { "name is required" }
    require(camelCase.length <= MAX_NAME_LENGTH) { "name too long: $camelCase" }
    require(!camelCase.contains("_")) { "camel case strings cannot contain _: $camelCase" }
    require(!camelCase.contains("-")) { "camel case strings cannot contain -: $camelCase" }

    val inputCase: CaseFormat
    if (camelCase.first().isLowerCase()) {
      inputCase = LOWER_CAMEL

      lowerCamel = camelCase
      upperCamel = inputCase.to(UPPER_CAMEL, camelCase)

    } else {
      inputCase = UPPER_CAMEL

      lowerCamel = UPPER_CAMEL.to(LOWER_CAMEL, camelCase)
      upperCamel = camelCase
    }

    lowerKebab = inputCase.to(LOWER_HYPHEN, camelCase)
    lowerSnake = inputCase.to(LOWER_UNDERSCORE, camelCase)
    upperSnake = inputCase.to(UPPER_UNDERSCORE, camelCase)
  }

  fun forCaseFormat(caseFormat: CaseFormat): String = when (caseFormat) {
    LOWER_CAMEL -> lowerCamel
    LOWER_HYPHEN -> lowerKebab
    LOWER_UNDERSCORE -> lowerSnake
    UPPER_CAMEL -> upperCamel
    UPPER_UNDERSCORE -> upperSnake
  }
}
