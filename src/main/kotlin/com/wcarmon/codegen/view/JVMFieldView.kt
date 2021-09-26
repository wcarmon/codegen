package com.wcarmon.codegen.view

import com.wcarmon.codegen.CREATED_TS_FIELD_NAMES
import com.wcarmon.codegen.UPDATED_TS_FIELD_NAMES
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage.JAVA_08
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
    field.effectiveBaseType(JAVA_08).isTemporal &&
        CREATED_TS_FIELD_NAMES.any {
          field.name.lowerCamel.equals(it, true)
        }

  val isUpdatedTimestamp: Boolean =
    field.effectiveBaseType(JAVA_08).isTemporal &&
        UPDATED_TS_FIELD_NAMES.any {
          field.name.lowerCamel.equals(it, true)
        }

  val shouldQuoteInString: Boolean = when (field.effectiveBaseType(JAVA_08)) {
    STRING -> true
    else -> false
  }

  val protobufTypeLiteral: String by lazy {
    field.effectiveTypeLiteral(JAVA_08)
  }

  val jacksonTypeRef by lazy {
    require(field.isParameterized(JAVA_08)) {
      "type references are only required for parameterized types"
    }

    val typeParameters = field.typeParameters(JAVA_08)

    when (field.effectiveBaseType(JAVA_08)) {
      LIST -> "List<${typeParameters[0]}>"
      SET -> "Set<${typeParameters[0]}>"
      else -> TODO("Build TypeReference for $this")
    }
  }

}
