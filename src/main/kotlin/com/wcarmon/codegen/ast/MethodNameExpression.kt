package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

data class MethodNameExpression(
  private val name: Name,
) : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ) = when (targetLanguage) {
    C_17 -> TODO()

    CPP_14,
    CPP_17,
    CPP_20,
    -> TODO()

    DART_2 -> TODO()

    GOLANG_1_7,
    JAVA_08,
    JAVA_11,
    JAVA_17,
    KOTLIN_JVM_1_4,
    -> name.lowerCamel

    PROTOCOL_BUFFERS_3 -> TODO()
    PYTHON_3 -> TODO()
    RUST_1_54 -> TODO()
    SWIFT_5 -> TODO()
    TYPESCRIPT_4 -> TODO()

    SQL_DB2 -> TODO()
    SQL_H2 -> TODO()
    SQL_MARIA -> TODO()
    SQL_MYSQL -> TODO()
    SQL_ORACLE -> TODO()
    SQL_POSTGRESQL -> TODO()
    SQL_SQLITE -> TODO()
  } + targetLanguage.statementTerminatorLiteral(terminate)
}
