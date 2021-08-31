package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage

/**
 * Configures rendering/serializing Expressions
 *
 * See [Expression]
 */
data class RenderConfig(
  val targetLanguage: TargetLanguage,

  val debugMode: Boolean = false,

  val lineIndentation: String = "",

  /**
   * Only affects languages which require statement terminators (eg. java, c, c++)
   *    converts an expression into a statement
   */
  val terminate: Boolean = targetLanguage.requiresStatementTerminator,
) {

  companion object {
    val INDENTATION = " ".repeat(2)
  }

  val indented: RenderConfig by lazy {
    copy(lineIndentation = lineIndentation + INDENTATION)
  }

  val doubleIndented: RenderConfig by lazy {
    copy(lineIndentation = lineIndentation + INDENTATION + INDENTATION)
  }

  val unindented: RenderConfig by lazy {
    copy(lineIndentation = "")
  }

  val unterminated: RenderConfig by lazy {
    copy(terminate = false)
  }

  val terminated: RenderConfig by lazy {
    copy(terminate = true)
  }

  val statementTerminatorLiteral by lazy {
    targetLanguage.statementTerminatorLiteral(terminate)
  }
}
