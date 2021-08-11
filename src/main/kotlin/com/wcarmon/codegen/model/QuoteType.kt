package com.wcarmon.codegen.model

enum class QuoteType {
  BACKTICK,
  DOUBLE,
  NONE,
  SINGLE,
  ;

  fun wrap(value: String) = when (this) {
    DOUBLE -> "\"$value\""
    SINGLE -> "'$value'"
    BACKTICK -> "`$value`"
    NONE -> value
  }
}
