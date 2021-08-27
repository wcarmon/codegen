package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.FieldReadMode.*
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Expression to read 1 field
 *
 * May or may not invoke a method (depends on the language)
 *
 * Uses appropriate name style for target language
 * Allows prefix (eg. "entity.getFoo()")
 * Allows (zero arg) getter or direct access (eg. "entity.foo" or "entity.getFoo()" )
 *
 * See also [MethodInvokeExpression]
 *
 * This is NOT related to Serde, see [SerdeReadExpression]
 */
data class FieldReadExpression(
  private val fieldName: Name,

  /** Kotlin allows non-null assertion (eg. !!) */
  private val assertNonNull: Boolean = false,

  /**
   * eg. "entity."
   * null implies fieldOwner==this
   * */
  private val fieldOwner: Expression? = null,

  private val overrideFieldReadStyle: FieldReadMode? = null,
) : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ) =
    getFieldReadPrefix(targetLanguage) +
        when (getFieldReadMode(targetLanguage)) {
          DIRECT -> getDirectFieldName(targetLanguage) + nonNullSnippet
          GETTER -> "get${fieldName.upperCamel}()"
        } +
        targetLanguage.statementTerminatorLiteral(terminate)

  private fun getFieldReadPrefix(
    targetLanguage: TargetLanguage,
  ) =
    if (fieldOwner == null) {
      ""
    } else {
      fieldOwner.render(targetLanguage, false) + "."
    }

  // Kotlin non-null assertion
  private val nonNullSnippet by lazy {
    if (assertNonNull) "!!"
    else ""
  }

  private fun getDirectFieldName(targetLanguage: TargetLanguage) =
    when (targetLanguage) {
      GOLANG_1_7,
      JAVA_08,
      JAVA_11,
      JAVA_17,
      KOTLIN_JVM_1_4,
      TYPESCRIPT_4,
      -> fieldName.lowerCamel

      SQL_DB2,
      SQL_H2,
      SQL_MARIA,
      SQL_MYSQL,
      SQL_ORACLE,
      SQL_POSTGRESQL,
      SQL_SQLITE,
      -> fieldName.lowerSnake

      C_17,
      CPP_14,
      CPP_17,
      CPP_20,
      DART_2,
      PROTOCOL_BUFFERS_3,
      PYTHON_3,
      RUST_1_54,
      SWIFT_5,
      -> TODO("what is the field read naming idiom for $targetLanguage")
    }

  private fun getFieldReadMode(targetLanguage: TargetLanguage) =
    overrideFieldReadStyle ?: defaultFieldReadMode(targetLanguage)

  private fun defaultFieldReadMode(targetLanguage: TargetLanguage) =
    when (targetLanguage) {
      C_17,
      CPP_14,
      CPP_17,
      CPP_20,
      GOLANG_1_7,
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
      TYPESCRIPT_4,
      -> DIRECT

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> GETTER

      DART_2 -> TODO("What is a good default FieldReadStyle for Dart?")
      SWIFT_5 -> TODO("What is a good default FieldReadStyle for Swift?")
    }
}
