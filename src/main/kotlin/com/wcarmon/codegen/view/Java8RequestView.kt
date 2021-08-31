package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.CodeGenRequest
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.util.consolidateImports

/**
 * Convenience methods/properties applicable to Java
 */
class Java8RequestView(
  private val debugMode: Boolean,
  private val request: CodeGenRequest,
) {

  fun importsForFieldsOnAllEntities(entities: Collection<Entity>): Set<String> =
    entities.flatMap {
      it.java8View.importsForFields
    }.toSortedSet()

  fun serializeImports(vararg importables: Any): String =
    consolidateImports(importables.toList())
      .joinToString(separator = "\n") {
        "import $it;"
      }
}
