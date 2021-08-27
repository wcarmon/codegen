package com.wcarmon.codegen.model

import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.ast.FieldReadMode.GETTER

enum class TargetLanguage {
  C_17,
  CPP_14,
  CPP_17,
  CPP_20,
  DART_2,
  GOLANG_1_7,
  JAVA_08,
  JAVA_11,
  JAVA_17,
  KOTLIN_JVM_1_4,
  PROTOCOL_BUFFERS_3,
  PYTHON_3,
  RUST_1_54,
  SQL_DB2,
  SQL_H2,
  SQL_MARIA,
  SQL_MYSQL,
  SQL_ORACLE,
  SQL_POSTGRESQL,
  SQL_SQLITE,
  SWIFT_5,
  TYPESCRIPT_4,
  ;

  val fieldReadMode by lazy {
    if (usesGetters) GETTER
    else DIRECT
  }

  val usesGetters by lazy {
    when (this) {
      C_17,
      GOLANG_1_7,
      KOTLIN_JVM_1_4,
      SQL_DB2,
      SQL_H2,
      SQL_MARIA,
      SQL_MYSQL,
      SQL_ORACLE,
      SQL_POSTGRESQL,
      SQL_SQLITE,
      TYPESCRIPT_4,
      -> false

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> true
      else -> TODO("does this language use getters?: $this")
    }
  }

  val onJVM by lazy {
    when (this) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      KOTLIN_JVM_1_4,
      -> true

      else -> false
    }
  }

  val isKotlin by lazy {
    when (this) {
      KOTLIN_JVM_1_4,
      -> true

      else -> false
    }
  }

  val isJava by lazy {
    when (this) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> true

      else -> false
    }
  }

  val requiresStatementTerminator by lazy {
    when (this) {
      C_17,
      CPP_14,
      CPP_17,
      CPP_20,
      DART_2,
      JAVA_08,
      JAVA_11,
      JAVA_17,
      PROTOCOL_BUFFERS_3,
      TYPESCRIPT_4,
      -> true

      KOTLIN_JVM_1_4,
      GOLANG_1_7,
      PYTHON_3,
      -> false

      RUST_1_54 -> TODO()

      SQL_DB2,
      SQL_H2,
      SQL_MARIA,
      SQL_MYSQL,
      SQL_ORACLE,
      SQL_POSTGRESQL,
      SQL_SQLITE,
      -> TODO()

      SWIFT_5 -> TODO()
    }
  }

  /**
   * Statement terminator
   *
   * See https://en.wikipedia.org/wiki/Comparison_of_programming_languages_(syntax)
   */
  fun statementTerminatorLiteral(shouldTerminate: Boolean): String =
    if (!shouldTerminate) {
      ""
    } else {
      when (this) {
        PYTHON_3 -> "\n"

        // -- semicolon inserted by compiler
        GOLANG_1_7,
        KOTLIN_JVM_1_4,
        SWIFT_5,
        -> ""

        else -> ";"
      }
    }

  val isSQL by lazy {
    when (this) {
      SQL_DB2,
      SQL_H2,
      SQL_MARIA,
      SQL_MYSQL,
      SQL_ORACLE,
      SQL_POSTGRESQL,
      SQL_SQLITE,
      -> true

      else -> false
    }
  }

  val isProtobuf by lazy {
    when (this) {
      PROTOCOL_BUFFERS_3 -> true
      else -> false
    }
  }
}
