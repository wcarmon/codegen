package com.wcarmon.codegen.ast

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.wcarmon.codegen.model.TargetLanguage.*

/** Represents javadoc, kdoc, godoc, rustdoc, jsdoc, tsdoc, cppdoc, ... */
data class DocumentationExpression(
  @JsonValue
  private val parts: List<String>,
) : Expression {

  companion object {
    @JvmStatic
    fun parse(vararg parts: String) =
      DocumentationExpression(parts.toList())

    @JvmStatic
    @JsonCreator
    fun parse(parts: Iterable<String>) =
      DocumentationExpression(parts.toList())

    val EMPTY = DocumentationExpression(listOf())
  }

  override val expressionName: String = DocumentationExpression::class.java.simpleName

  val isBlank: Boolean = parts.isEmpty() || parts.all { it.isBlank() }

  val isNotBlank: Boolean = parts.isNotEmpty() && parts.any { it.isNotBlank() }

  override fun renderWithoutDebugComments(config: RenderConfig): String =
    when (config.targetLanguage) {

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4,
      -> handleKotlin(config)

      else -> TODO("render documentation: config=$config, parts=$parts")
    }

  //TODO: handle wrapping references in {@link ...}
  private fun handleJava(config: RenderConfig): String {
    if (isBlank) {
      return ""
    }

    return parts.joinToString(
      prefix = "${config.lineIndentation}/**\n",
      separator = "\n ",
      postfix = "\n${config.lineIndentation}*/",
    ) {
      "${config.lineIndentation} * $it"
    }
  }

  //TODO: handle wrapping references in [...]
  private fun handleKotlin(config: RenderConfig): String {
    if (isBlank) {
      return ""
    }

    return parts.joinToString(
      prefix = "${config.lineIndentation}/**\n",
      separator = "\n ",
      postfix = "\n*/",
    ) {
      "${config.lineIndentation} * $it"
    }
  }
}
