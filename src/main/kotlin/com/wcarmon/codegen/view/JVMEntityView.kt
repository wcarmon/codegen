package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Entity

/**
 * Convenience methods/properties applicable across JVM languages
 */
class JVMEntityView(
  entity: Entity,
) {

  val requiresObjectWriter =
    entity.fields.any { it.effectiveBaseType.isCollection }

  val requiresObjectReader =
    entity.fields.any { it.effectiveBaseType.isCollection }

}


