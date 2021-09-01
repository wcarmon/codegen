package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.ProtoFieldDeclarationExpression.Companion.NUMBER_COMPARATOR
import com.wcarmon.codegen.model.Name

/**
 * See https://developers.google.com/protocol-buffers/docs/proto3#simple
 */
data class ProtoMessageDeclarationExpression(
  private val name: Name,

  private val enums: List<ProtoEnumDeclarationExpression> = listOf(),
  private val fields: Collection<ProtoFieldDeclarationExpression> = listOf(),

  //TODO: support reserved fields
  // https://developers.google.com/protocol-buffers/docs/proto3#reserved
) : Expression {

  override val expressionName: String = ProtoMessageDeclarationExpression::class.java.simpleName

  init {

    val fieldNumbers = fields.map { it.number }
    require(fieldNumbers.toSet().size == fieldNumbers.size) {
      "fieldNumbers must be unique: $fields"
    }
  }

  private val sortedFieldExpressions: List<ProtoFieldDeclarationExpression> =
    fields.sortedWith(NUMBER_COMPARATOR)

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ): String {
    check(config.targetLanguage.isProtobuf)

    //TODO: documentation on top
    return """
    |message ${name.upperCamel}{
    ${renderFields(config)}
    |  
    ${renderEnums(config)}  
    |}
    """.trimMargin()

    //TODO: prefix every lines with lineIndentation
  }

  private fun renderFields(config: RenderConfig): String =
    sortedFieldExpressions.joinToString("\n") {
      "|  ${it.render(config.terminated)}"
    }

  private fun renderEnums(config: RenderConfig): String =
    enums.joinToString("\n") {
      "|  ${it.render(config)}"
    }
}
