package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.ProtoFieldDeclarationExpression
import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
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
    buildFieldDeclarations(entity.sortedFieldsWithIdsFirst)
  }

  fun idFieldDeclarations(
    startingIndex: Int,
    lineIndentation: String,
  ): String =
    buildFieldDeclarations(
      entity.idFields,
      startingIndex
    )


  fun fieldsForPatch(
    fieldToPatch: Field,
    startingIndex: Int = 1,
    lineIndentation: String,
  ): String =
    buildFieldDeclarations(
      entity.idFields + fieldToPatch,
      startingIndex
    )

  fun buildFieldDeclarations(
    fields: List<Field>,
    firstIndex: Int = 1,
  ): String =
    fields
      .mapIndexed { index, field ->

        ProtoFieldDeclarationExpression(
          deprecated = false,
          field = field,
          number = ProtoFieldNumber(index + firstIndex),
          repeated = field.isCollection,
        ).render(renderConfig.indented)
      }
      .joinToString(
        separator = "\n"
      )
}
