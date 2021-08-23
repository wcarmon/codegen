package com.wcarmon.codegen.model

/**
 * Use getter or read directly
 */
enum class FieldReadStyle {
  /** myEntity.theField */
  DIRECT,

  /** myEntity.getTheField() */
  GETTER
}
