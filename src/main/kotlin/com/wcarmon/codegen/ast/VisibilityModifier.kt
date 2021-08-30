package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Applies to class, method, function, field
 *
 * Golang: case drives visibility
 * Java: public|private|protected|(package)
 * Kotlin: public|private|protected|internal
 */
enum class VisibilityModifier {
  /**
   * Kotlin: Only visible with the module (eg. Maven/Gradle project)
   */
  INTERNAL,

  /**
   * Java: Only visible with the class
   * Kotlin:
   */
  PRIVATE,

  /**
   * Visible to subclasses
   */
  PROTECTED,

  /**
   * Visible to all classes everywhere
   */
  PUBLIC,
  ;

  fun render(targetLanguage: TargetLanguage): String {
    return when (targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava()

      KOTLIN_JVM_1_4,
      -> handleKotlin()
      else -> TODO("handle rendering: $this")
    }
  }

  private fun handleJava() = when (this) {
    INTERNAL -> throw IllegalStateException("Java dosn't support INTERNAL visibility modifier")
    PRIVATE -> "private"
    PROTECTED -> "protected"
    PUBLIC -> "public"
  }

  private fun handleKotlin() = when (this) {
    INTERNAL -> "internal"
    PRIVATE -> "private"
    PROTECTED -> "protected"
    PUBLIC -> ""
  }
}
