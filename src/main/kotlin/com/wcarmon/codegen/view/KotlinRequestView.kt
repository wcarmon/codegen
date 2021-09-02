package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.CodeGenRequest
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.util.consolidateImports

/**
 * Convenience methods/properties applicable to Kotlin
 */
class KotlinRequestView(
  private val debugMode: Boolean,
  private val request: CodeGenRequest,
) {

  fun importsForFieldsOnAllEntities(entities: Collection<Entity>): Set<String> =
    entities.flatMap {
      it.kotlinView.importsForFields
    }.toSortedSet()

  fun serializeImports(vararg importables: Any): String =
    consolidateImports(importables.toList())
      .joinToString(separator = "\n") {
        "import $it"
      }
}
