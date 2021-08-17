package com.wcarmon.codegen.model.ast

//TODO: inline class?
// is FunctionName better?
data class MethodName(
  val value: String,
) {

  init {
    require(value.isNotBlank()) { "name cannot be blank" }

    //TODO: max length
    //TODO: acceptable chars (regex):  maybe [a-zA-Z_][a-zA-Z0-9_]+{40}
    //TODO: language specific validation goes here
  }

  override fun toString() = value
}
