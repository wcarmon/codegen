package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.CodeGenRequest
import com.wcarmon.codegen.util.consolidateImports

class GolangRequestView(
  private val debugMode: Boolean,
  private val request: CodeGenRequest,
) {


  val templateDebugInfo by lazy {
    if (debugMode) {
      """
      |// templatePath = ${request.prettyTemplateName}
      |// timestamp    = ${java.time.Instant.now()}
      """.trimMargin()
    } else {
      ""
    }
  }

  fun serializeImports(vararg importables: Any): String =
    consolidateImports(importables.toList())
      .joinToString(
        prefix = "import (\n",
        postfix = "\n)",
        separator = "\n") {
        "    \"$it\""
      }
}
