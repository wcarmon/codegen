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

  fun serializeImports(vararg importables: Any): String {
    val indentation = " ".repeat(4)

    return consolidateImports(importables.toList())
      .joinToString(
        prefix = "import (\n",
        postfix = "\n)",
        separator = "\n"
      ) {

        indentation +
            if (it.contains("\"")) {
              it
            } else {
              "\"$it\""
            }
      }
  }
}
