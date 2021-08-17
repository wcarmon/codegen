package com.wcarmon.codegen.model

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
  PYTHON_3,
  RUST_1_54,
  SQL,
  SWIFT_5,
  TYPESCRIPT_4,
  ;

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
      TYPESCRIPT_4,
      -> true

      KOTLIN_JVM_1_4,
      GOLANG_1_7,
      PYTHON_3,
      -> false

      RUST_1_54 -> TODO()
      SQL -> TODO()
      SWIFT_5 -> TODO()
    }
  }
}
