package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.PROTO_BUF_3

/**
 * Protobuf related convenience methods for a [Field]
 *
 * See [com.wcarmon.codegen.model.ProtobufFieldConfig]
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

  /**
   * @return Name of setter method on builder
   */
  val protobufSetterMethodName: Name by lazy {
    val prefix =
      if (field.effectiveBaseType(PROTO_BUF_3).isCollection ||
        field.protobufConfig.repeated
      ) {
        "addAll"
      } else {
        "set"
      }

    Name("$prefix${field.name.upperCamel}")
  }


  /**
   * Method name on a proto to retrieve a field
   */
  val protobufGetterMethodName: Name by lazy {
    val suffix =
      if (field.effectiveBaseType(PROTO_BUF_3).isCollection ||
        field.protobufConfig.repeated
      ) {
        "List"
      } else {
        ""
      }

    Name("get${field.name.upperCamel}$suffix")
  }


  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    lineIndentation = "",
    targetLanguage = targetLanguage,
    terminate = true,
  )
}
