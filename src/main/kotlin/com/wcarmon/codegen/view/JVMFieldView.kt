package com.wcarmon.codegen.view

import com.wcarmon.codegen.CREATED_TS_FIELD_NAMES
import com.wcarmon.codegen.UPDATED_TS_FIELD_NAMES
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.util.defaultValueLiteralForJVM

/**
 * Convenience methods/properties applicable across JVM languages
 */
class JVMFieldView(
  private val debugMode: Boolean,
  private val field: Field,
) {

  val defaultValueLiteral: String by lazy {
    defaultValueLiteralForJVM(field)
  }

  val isCreatedTimestamp: Boolean =
    field.jvmConfig.effectiveBaseType.isTemporal &&
        CREATED_TS_FIELD_NAMES.any {
          field.name.lowerCamel.equals(it, true)
        }

  val isUpdatedTimestamp: Boolean =
    field.jvmConfig.effectiveBaseType.isTemporal &&
        UPDATED_TS_FIELD_NAMES.any {
          field.name.lowerCamel.equals(it, true)
        }

  val shouldQuoteInString: Boolean = when (field.jvmConfig.effectiveBaseType) {
    STRING -> true
    else -> false
  }

  val jacksonTypeRef by lazy {
    require(field.jvmConfig.isParameterized) {
      "type references are only required for parameterized types"
    }

    when (field.jvmConfig.effectiveBaseType) {
      LIST -> "List<${field.jvmConfig.typeParameters[0]}>"
      SET -> "Set<${field.jvmConfig.typeParameters[0]}>"
      else -> TODO("Build TypeReference for $this")
    }
  }

}
