package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.protoBuilderSetter

/**
 * RDBMS related convenience methods for a [Field]
 * See [com.wcarmon.codegen.model.RDBMSColumnConfig]
 */
data class ProtobufFieldView(
  private val field: Field,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isProtobuf) {
      "invalid target language: $targetLanguage"
    }
  }

  //TODO: rename
  val builderSetter = protoBuilderSetter(field).value
}
