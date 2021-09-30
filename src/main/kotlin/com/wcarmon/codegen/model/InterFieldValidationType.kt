package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonValue

enum class InterFieldValidationType(
  @JsonValue
  val label: String,
) {

  /**
   * Enforce:
   * field0 is before field1
   * field1 is after field0
   */
  BEFORE("before"),

  /**
   * field0 <= field1
   * field1 > field0
   */
  LESS_OR_EQUAL_TO("lessOrEqualTo"),

  /**
   * field0 < field1
   * field1 >= field0
   */
  LESS_THAN("lessThan"),

  /**
   * Enforce:
   * field0 is same or after field1
   * field1 is same or before field0
   */
  NOT_BEFORE("notBefore"),

  /**
   * Enforce:
   * field0 and field1 are different
   */
  NOT_EQUAL("notEqual"),
}
