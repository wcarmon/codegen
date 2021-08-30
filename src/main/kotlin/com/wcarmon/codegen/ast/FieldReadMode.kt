package com.wcarmon.codegen.ast

/**
 * Use getter or read directly
 */
enum class FieldReadMode {

  /** myEntity.theField */
  DIRECT,

  /** myEntity.getTheField() */
  GETTER
}
