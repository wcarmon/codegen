package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * java:        x == null ? defaultValue : x
 * kotlin:      x ?: defaultValue
 * typescript:  x ?? defaultValue
 * SQL:         NVL(...)
 * Golang:      ...TODO...
 *
 * See https://docs.oracle.com/cd/B19306_01/server.102/b14200/functions105.htm
 */
data class DefaultWhenNullExpression(
  private val primaryExpression: Expression,
  private val defaultValueExpression: DefaultValueExpression,
) : Expression {

  override val expressionName: String = DefaultWhenNullExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig): String {

    val renderedDefault = defaultValueExpression.render(config)
    check(renderedDefault.isNotBlank()) {
      "${DefaultWhenNullExpression::class.java.simpleName} only useful when defaultValue.isPresent"
    }

    return when (config.targetLanguage) {
      GOLANG_1_9,
      -> handleGolang(config)

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4,
      -> handleKotlin(config)

      else -> TODO("handle default-when-null for targetLanguage=${config.targetLanguage}")
    }
  }

  private fun handleGolang(config: RenderConfig): String {
    val renderedPrimary = primaryExpression.render(config)
    val renderedDefault = defaultValueExpression.render(config)

    TODO("Not yet implemented")
  }

  private fun handleJava(config: RenderConfig): String {

    val renderedPrimary = primaryExpression.render(config)
    val renderedDefault = defaultValueExpression.render(config)

    return config.lineIndentation +
        "null == " +
        renderedPrimary +
        " ? " +
        renderedDefault +
        renderedPrimary +
        config.statementTerminatorLiteral
  }

  private fun handleKotlin(config: RenderConfig): String {
    val renderedPrimary = primaryExpression.render(config)
    val renderedDefault = defaultValueExpression.render(config)

    return config.lineIndentation +
        renderedPrimary +
        " ?: " +
        renderedDefault
  }
}
