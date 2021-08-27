package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage

/**
 * See https://developers.google.com/protocol-buffers/docs/proto3#enum
 */
data class ProtoEnumDeclarationExpression(
  val items: List<String>,
  val name: Name,

  /** Defaults to undefined, instead of first element */
  val prependUndefinedElement: Boolean = true,
) : Expression {

  companion object {
    const val UNDEFINED_ITEM: String = "UNDEFINED"
  }

  init {
    require(items.size < Int.MAX_VALUE) {
      "too many items in enum: ${items.size}"
    }

    if (prependUndefinedElement) {
      require(items.isNotEmpty()) {
        "at least one item required in enum"
      }

    } else {
      require(items.size > 1) {
        "at least one item required in enum (first item is default)"
      }
    }
  }

  private val formattedItems: List<String> = items
    .map { Name(it).upperSnake }

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ): String {
    check(targetLanguage.isProtobuf)

    return """
    |enum ${name.upperCamel} {
    ${renderedItems}  
    |}  
    """.trimMargin()
  }

  private val renderedItems: String by lazy {
    val offset: Int
    val renderedItems: MutableList<String> = mutableListOf()

    if (prependUndefinedElement) {
      offset = 1
      renderedItems += "|  $UNDEFINED_ITEM = 0;"
    } else {
      offset = 0
    }

    renderedItems +=
      formattedItems.mapIndexed { index, item ->
        "|  $item = ${index + offset};"
      }

    renderedItems.joinToString("\n")
  }
}
