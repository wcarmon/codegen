package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.ProtoFieldDeclarationExpression
import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.ProtoFieldNumber
import com.wcarmon.codegen.model.TargetLanguage

/**
 * Protobuf related convenience methods for a [Entity]
 */
class ProtobufEntityView(
  debugMode: Boolean,
  private val entity: Entity,
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

  val fieldDeclarations: String by lazy {

    entity.sortedFieldsWithIdsFirst
      .mapIndexed { index, field ->
        ProtoFieldDeclarationExpression(
          deprecated = false,
          field = field,
          number = ProtoFieldNumber(index + 1),
          repeated = field.isCollection,
        ).render(renderConfig.indented)
      }
      .joinToString(
        separator = "\n"
      )
  }
}
