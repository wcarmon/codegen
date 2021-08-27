package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.protoBuilderSetter
import com.wcarmon.codegen.util.buildProtoBufMessageFieldDeclarations

/**
 * RDBMS related convenience methods for a [Field]
 * See [com.wcarmon.codegen.model.RDBMSColumnConfig]
 */
class ProtobufFieldView(
  private val field: Field,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isProtobuf) {
      "invalid target language: $targetLanguage"
    }
  }

  //TODO: rename
  val builderSetter: String = protoBuilderSetter(field).value

  val protocolBufferFields: String by lazy {
    //TODO: use [ProtoMessageDeclarationExpression] instead
    buildProtoBufMessageFieldDeclarations(
      primaryKeyFields + nonPrimaryKeyFields
    )
      .map { "  " + it.render(TargetLanguage.PROTOCOL_BUFFERS_3) }
      .joinToString("\n")
  }

}
