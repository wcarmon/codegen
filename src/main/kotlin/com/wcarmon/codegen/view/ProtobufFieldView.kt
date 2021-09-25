package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.effectiveProtobufType

/**
 * Protobuf related convenience methods for a [Field]
 *
 * See [com.wcarmon.codegen.model.ProtoBufFieldConfig]
 */
class ProtobufFieldView(
  debugMode: Boolean,
  private val field: Field,
  targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isProtobuf) {
      "invalid target language: $targetLanguage"
    }
  }

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    lineIndentation = "",
    targetLanguage = targetLanguage,
    terminate = true,
  )

  val typeLiteral: String by lazy {
    effectiveProtobufType(field)
  }
}
