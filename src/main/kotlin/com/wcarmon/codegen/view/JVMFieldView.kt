package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.util.defaultValueLiteralForJVM

/**
 * Convenience methods/properties applicable across JVM languages
 */
class JVMFieldView(
  private val field: Field,
) {

  val defaultValueLiteralForJVM: String? by lazy {
    defaultValueLiteralForJVM(field)
  }

  val jacksonTypeRef by lazy {
    require(field.type.isParameterized) {
      "type references are only required for parameterized types"
    }

    when (field.effectiveBaseType) {
      BaseFieldType.LIST -> "List<${field.type.typeParameters[0]}>"
      BaseFieldType.SET -> "Set<${field.type.typeParameters[0]}>"
      else -> TODO("Build TypeReference for $this")
    }
  }

}
