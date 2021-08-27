package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage

/**
 * See https://developers.google.com/protocol-buffers/docs/proto3#simple
 */
//TODO: enforce distinct field order
data class ProtoMessageDeclarationExpression(
  val name: Name,

  val enums: List<ProtoEnumDeclarationExpression> = listOf(),
  val fields: List<ProtoFieldDeclarationExpression> = listOf(),

  //TODO: support reserved fields
  // https://developers.google.com/protocol-buffers/docs/proto3#reserved
) : Expression {


  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
  ): String {
    check(targetLanguage.isProtobuf)

    //TODO: documentation on top
    return """
    |message ${name.upperCamel}{
    ${renderFields(targetLanguage)}
    |  
    ${renderEnums(targetLanguage)}  
    |}
    """.trimMargin()
  }

  private fun renderFields(targetLanguage: TargetLanguage): String =
    fields.map {
      "|  ${it.render(targetLanguage, true)}"
    }
      .joinToString("\n")

  private fun renderEnums(targetLanguage: TargetLanguage): String =
    enums.map {
      "|  ${it.render(targetLanguage)}"
    }
      .joinToString("\n")
}
