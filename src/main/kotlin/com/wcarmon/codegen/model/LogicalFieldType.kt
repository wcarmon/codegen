package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator

class LogicalFieldType(
  val base: BaseFieldType,
  val nullable: Boolean = false,

  // -- Only numeric types
  val precision: Int, // total # significant digits (both sides of decimal point)
  val scale: Int = 0, // # decimal digits
  val signed: Boolean = true,
) {

  companion object {

    /**
     * Supported prefixes:
     *  "golang."
     *  "java.lang."
     *  "java.math."
     *  "java.sql."
     *  "java.time."
     *  "kotlin."
     *  "maria."
     *  "mysql."
     *  "postgres."
     *
     * See [LogicalFieldTypeTest] for other supported values
     */
    @JvmStatic
    @JsonCreator
    fun parse(literal: String): LogicalFieldType = TODO()

    fun isSigned(literal: String): Boolean {
      TODO()
    }
  }

  init {
    require(precision <= 1000) { "precision too large: $precision" }
    require(scale >= 0) { "Scale too low: $scale" }

    if (base.isNumeric()) {
      require(precision >= 0) { "Precision too low: $precision" }
    } else {
      require(precision == 0) { "only numeric types can have precision" }
    }

    //TODO: integer types always have scale=0
    //TODO: max precision
    //TODO: min precision
    //TODO: min scale
    //TODO: max scale
  }


  fun asC(): String {
    TODO()
  }

  fun asDart(): String {
    TODO()
  }

  fun asGolang(): String {
    TODO()
  }

  fun asJava(): String {
    TODO()
  }

  fun asJS(): String {
    TODO()
  }

  fun asKotlin(): String {
    TODO()
  }

  fun asRust(): String {
    TODO()
  }

  fun asSQL(): String {
    TODO()
  }

  fun asSwift(): String {
    TODO()
  }

  fun asVala(): String {
    TODO()
  }
}
