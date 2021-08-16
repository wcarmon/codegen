package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.TargetLanguage

/**
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
fun interface Expression {

  /** Serialize to Java code */
  fun serialize(targetLanguage: TargetLanguage): String
}
