package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.TargetLanguage.JAVA_08

/**
 * Convenience methods/properties applicable across JVM languages
 */
class JVMEntityView(
  entity: Entity,
  private val debugMode: Boolean,
) {

  val requiresObjectWriter =
    entity.fields.any { it.effectiveBaseType(JAVA_08).isCollection }

  val requiresObjectReader =
    entity.fields.any { it.effectiveBaseType(JAVA_08).isCollection }

  val commaSeparatedIDFieldNames: String by lazy {
    entity.idFields.joinToString(", ") { it.name.lowerCamel }
  }

  val collectionFields = entity.fields
    .filter { it.effectiveBaseType(JAVA_08).isCollection }
    .sortedBy { it.name.lowerCamel }
}


