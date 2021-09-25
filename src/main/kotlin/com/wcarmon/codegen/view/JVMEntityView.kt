package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Entity

/**
 * Convenience methods/properties applicable across JVM languages
 */
class JVMEntityView(
  entity: Entity,
  private val debugMode: Boolean,
) {

  val requiresObjectWriter =
    entity.fields.any { it.jvmConfig.effectiveBaseType.isCollection }

  val requiresObjectReader =
    entity.fields.any { it.jvmConfig.effectiveBaseType.isCollection }

  val commaSeparatedIDFieldNames: String by lazy {
    entity.idFields.joinToString(", ") { it.name.lowerCamel }
  }

  val collectionFields = entity.fields
    .filter { it.jvmConfig.effectiveBaseType.isCollection }
    .sortedBy { it.name.lowerCamel }
}


