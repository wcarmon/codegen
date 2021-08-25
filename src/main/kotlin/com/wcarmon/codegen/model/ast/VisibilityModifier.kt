package com.wcarmon.codegen.model.ast

/**
 * Applies to class, method, function, field
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
}
