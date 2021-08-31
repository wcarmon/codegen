package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.CodeGenRequest

/**
 * Convenience methods/properties applicable across JVM languages
 */
//TODO: works on everything except json and python
class JVMRequestView(
  private val debugMode: Boolean,
  private val request: CodeGenRequest,
) {

  val templateNameComment by lazy {
    if (debugMode) {
      "/*\ntemplatePath = ${request.prettyTemplateName} \n*/"
    } else {
      ""
    }
  }

  val contextClass: String = request.jvmContextClass

  val extraImports = request.extraJVMImports

  val hasContextClass = request.jvmContextClass.isNotBlank()

  val sortedExtraJVMImports = request.extraJVMImports.sorted()

  val unqualifiedContextClass = request.jvmContextClass.substringAfterLast(".")
}
