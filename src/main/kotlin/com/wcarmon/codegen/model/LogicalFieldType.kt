package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator

class LogicalFieldType(
  val base: BaseFieldType,
  val nullable: Boolean = false,
  val signed: Boolean = true, // only numeric types
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
