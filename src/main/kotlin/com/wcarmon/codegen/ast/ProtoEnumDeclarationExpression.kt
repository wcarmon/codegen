package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name

/**
 * See https://developers.google.com/protocol-buffers/docs/proto3#enum
 */
data class ProtoEnumDeclarationExpression(
  private val items: List<String>,
  private val name: Name,

  /** Defaults to undefined, instead of first element */
  private val prependUndefinedElement: Boolean = true,
) : Expression {

  override val expressionName = ProtoEnumDeclarationExpression::class.java.name

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
    config: RenderConfig,
  ): String {
    check(config.targetLanguage.isProtobuf)

    //TODO: use lineIndentation
    return """
    |enum ${name.upperCamel} {
    $renderedItems  
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

    //TODO: use lineIndentation
    renderedItems.joinToString("\n")
  }
}
