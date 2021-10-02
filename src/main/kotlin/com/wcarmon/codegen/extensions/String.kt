package com.wcarmon.codegen.extensions

val String.escapeDoubleQuotes: String
  get() = this.replace("\"", "\\\"")

fun String.whenBlank(alternative: () -> String) =
  if (this.isBlank()) {
    alternative()
  } else {
    this
  }
