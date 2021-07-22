package com.wcarmon.codegen.input

import com.wcarmon.codegen.model.Entity

/**
 * Builds an appropriate output file name from [Entity]
 * "Appropriate" depends on the template, target-language, etc
 */
fun interface OutputFileNameBuilder {

  /**
   * Use the entity to build a file name
   *
   * Generator will prefix the output file name with correct directory
   *
   * @return the file name.
   */
  fun build(entity: Entity): String
}
