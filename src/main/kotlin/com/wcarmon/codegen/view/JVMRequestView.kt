package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.CodeGenRequest

/**
 * Convenience methods/properties applicable across JVM languages
 */
class JVMRequestView(
  private val request: CodeGenRequest,
) {

  val contextClass: String = request.jvmContextClass

  val extraImports = request.extraJVMImports

  val hasContextClass = request.jvmContextClass.isNotBlank()

  val sortedExtraJVMImports = request.extraJVMImports.sorted()

  val unqualifiedContextClass = request.jvmContextClass.substringAfterLast(".")
}
