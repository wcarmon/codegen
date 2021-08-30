package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage

/**
 * The atomic "lego block" for a reusable piece of code
 * - generally configurable
 * - responsible for rendering/serializing itself
 * - responsible for formatting itself (based on config)
 * - generally language independent
 *
 * Older Languages (like Java) consider some "Expressions" as Statements
 *
 * Expression:
 *  describes a value, evaluated to produce a result/value
 *  eg.
 *    - method call
 *    - object allocation
 *    - variable assignment (in java, not kotlin)
 *    - ...
 *
 * Statement:
 *  functional languages have zero statements
 *  eg.
 *  - variable declaration
 *  - variable assignment
 *  - class declaration
 *
 * See https://www.oreilly.com/library/view/learning-java/1565927184/ch04s04.html
 * See https://blog.kotlin-academy.com/kotlin-programmer-dictionary-statement-vs-expression-e6743ba1aaa0
 */
interface Expression {

  /**
   * Render/Serialize to Kotlin/Java/Golang/Rust/Protobuf... code snippet
   * @param targetLanguage
   * @param terminate only affects languages which require statement terminators (eg. java, c, c++)
   *    converts an expression into a statement
   * @param lineIndentation
   */
  fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String = "",
  ): String

  /** Convenience helper */
  fun render(
    targetLanguage: TargetLanguage,
  ) = render(
    targetLanguage = targetLanguage,
    terminate = targetLanguage.requiresStatementTerminator,
    lineIndentation = "")

  fun isEmpty(targetLanguage: TargetLanguage) =
    this.render(targetLanguage, false, "")
      .isEmpty()

  fun isBlank(targetLanguage: TargetLanguage) =
    this.render(targetLanguage, false, "")
      .isBlank()
}
