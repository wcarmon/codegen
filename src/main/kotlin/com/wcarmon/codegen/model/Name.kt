package com.wcarmon.codegen.model

import com.wcarmon.codegen.MAX_NAME_LENGTH

/**
 * Identifier for Entity or Field
 */
data class Name(
  val camelCase: String,
) {
  init {
    require(camelCase.isNotBlank()) { "name is required" }
    require(camelCase.length <= MAX_NAME_LENGTH) { "name too long: $camelCase" }
  }

  // TODO: convert to other forms

  val lowerCamel = TODO() as String
  val lowerKebab = TODO() as String
  val lowerSnake = TODO() as String
  val upperCamel = TODO() as String
  val upperKebab = TODO() as String
  val upperSnake = TODO() as String
}
