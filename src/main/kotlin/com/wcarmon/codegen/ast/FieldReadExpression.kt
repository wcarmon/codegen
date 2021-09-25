package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.ast.FieldReadMode.GETTER
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Expression to read 1 field
 *
 * May or may not invoke a method (depends on the language)
 *
 * Uses appropriate name style for target language
 *
 * Examples:
 *  - foo
 *  - getFoo()
 *  - x.foo
 *  - x.Foo
 *  - x.GetFoo()
 *  - x.getFoo()
 *
 * See also [MethodInvokeExpression]
 *
 * This is NOT related to Serde, see [WrappedExpression] or [WrapWithSerdeExpression]
 */
data class FieldReadExpression(
  private val fieldName: Name,

  /** Kotlin allows non-null assertion (eg. !!) */
  private val assertNonNull: Boolean = false,

  /**
   * eg. "entity."
   * null implies fieldOwner==this
   * */
  private val fieldOwner: Expression = EmptyExpression,

  private val overrideFieldReadMode: FieldReadMode? = null,
) : Expression {

  override val expressionName: String = FieldReadExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ) =
    getFieldReadPrefix(config) +
        when (getFieldReadMode(config)) {
          DIRECT -> getDirectFieldName(config.targetLanguage) + nonNullSnippet
          GETTER -> "get${fieldName.upperCamel}()"
        } +
        config.statementTerminatorLiteral

  private fun getFieldReadPrefix(
    config: RenderConfig,
  ): String {

    val output = fieldOwner.render(config.unterminated)

    return if (output.isBlank()) {
      ""
    } else {
      "${output}."
    }
  }

  // Kotlin non-null assertion
  private val nonNullSnippet by lazy {
    if (assertNonNull) "!!"
    else ""
  }

  private fun getDirectFieldName(targetLanguage: TargetLanguage) =
    when (targetLanguage) {
      GOLANG_1_8,
      JAVA_08,
      JAVA_11,
      JAVA_17,
      KOTLIN_JVM_1_4,
      TYPESCRIPT_4,
      -> fieldName.lowerCamel

      SQL_DB2,
      SQL_DELIGHT,
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

  private fun getFieldReadMode(config: RenderConfig) =
    overrideFieldReadMode ?: defaultFieldReadMode(config.targetLanguage)

  private fun defaultFieldReadMode(targetLanguage: TargetLanguage) =
    when (targetLanguage) {
      C_17,
      CPP_14,
      CPP_17,
      CPP_20,
      GOLANG_1_8,
      KOTLIN_JVM_1_4,
      PROTOCOL_BUFFERS_3,
      PYTHON_3,
      RUST_1_54,
      SQL_DB2,
      SQL_DELIGHT,
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

      DART_2 -> TODO("What is a good default FieldReadMode for Dart?")
      SWIFT_5 -> TODO("What is a good default FieldReadMode for Swift?")
    }
}
