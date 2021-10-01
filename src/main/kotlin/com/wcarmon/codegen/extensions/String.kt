package com.wcarmon.codegen.extensions

val String.escapeDoubleQuotes: String
  get() = this.replace("\"", "\\\"")
