package com.wcarmon.codegen.model

/**
 * Validation across 2 fields
 */
data class InterFieldValidation(
  val fieldName0: Name,
  val fieldName1: Name,
  val type: InterFieldValidationType
) {

  init {
    require(fieldName0 != fieldName1) {
      "InterFieldValidation requires different fields: fieldName0=${fieldName0}, fieldName1=${fieldName1}"
    }
  }
}
