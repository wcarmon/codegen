package com.wcarmon.codegen.model


enum class FieldReadStyle {
  /** myEntity.theField */
  DIRECT,

  /** myEntity.getTheField() */
  GETTER
}
