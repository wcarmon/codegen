package com.wcarmon.codegen.model.ast

/**
 * Use getter or read directly
 */
enum class FieldReadMode {
  /** myEntity.theField */
  DIRECT,

  /** myEntity.getTheField() */
  GETTER
}
