package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator

class LogicalFieldType(
  val base: BaseFieldType,
  val nullable: Boolean = false,

  // -- Only numeric types
  val precision: Int = 0, // total # significant digits (both sides of decimal point)
  val scale: Int = 0,     // # decimal digits
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
    require(precision <= 1_000) { "precision too high: $precision" }
    require(precision >= 0) { "precision too low: $precision" }

    require(scale <= precision) { "Scale too high: scale=$scale, precision=$precision" }
    require(scale >= 0) { "Scale too low: $scale" }

    if (base.requiresPrecision()) {
      require(precision > 0) { "Precision too low: $precision" }
    } else {
      require(precision == 0) { "Only numeric types can have precision" }
    }

    if (!base.canHaveScale()) {
      //TODO: missing context
      require(scale == 0) { "field cannot have scale" }
    }
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
