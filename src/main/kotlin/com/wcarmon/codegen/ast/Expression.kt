package com.wcarmon.codegen.ast

import com.wcarmon.codegen.util.wrapWithExpressionTracingComments

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

  /** Useful for Tracing & Debugging */
  val expressionName: String

  /**
   * Render/Serialize to Kotlin/Java/Golang/Rust/Protobuf... code snippet
   *
   * No tracing, no debugging comments
   */
  fun render(config: RenderConfig): String

  fun isEmpty(config: RenderConfig) =
    render(config)
      .isEmpty()

  fun isBlank(config: RenderConfig) =
    render(config)
      .isBlank()

  /**
   * Conditionally wraps rendered expression with tracing/debug comments
   *
   * [config] controls tracing
   */
  fun renderWithConditionalTracing(
    config: RenderConfig,
  ): String =
    if (!config.debugMode) {
      render(config)

    } else {
      wrapWithExpressionTracingComments(
        config = config,
        expressionName = expressionName,
        renderedCode = render(config),
      )
    }
}
