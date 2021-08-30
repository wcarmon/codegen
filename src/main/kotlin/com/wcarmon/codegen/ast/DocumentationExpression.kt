package com.wcarmon.codegen.ast

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.wcarmon.codegen.model.TargetLanguage
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

  val isBlank: Boolean = parts.isEmpty() || parts.all { it.isBlank() }

  val isNotBlank: Boolean = parts.isNotEmpty() && parts.any { it.isNotBlank() }

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ): String = when (targetLanguage) {

    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> handleJava(lineIndentation)

    KOTLIN_JVM_1_4,
    -> handleKotlin(lineIndentation)

    else -> TODO("render documentation: language=$targetLanguage, parts=$parts")
  }

  //TODO: handle wrapping references in {@link ...}
  private fun handleJava(
    lineIndentation: String = "",
  ): String {
    if (isBlank) {
      return ""
    }

    return parts.joinToString(
      postfix = "\n*/",
      prefix = "/**\n",
      separator = "\n ",
    ) {
      "$lineIndentation * $it"
    }
  }

  //TODO: handle wrapping references in [...]
  private fun handleKotlin(
    lineIndentation: String = "",
  ): String {
    if (isBlank) {
      return ""
    }

    return parts.joinToString(
      postfix = "\n*/",
      prefix = "/**\n",
      separator = "\n ",
    ) {
      "$lineIndentation * $it"
    }
  }
}
