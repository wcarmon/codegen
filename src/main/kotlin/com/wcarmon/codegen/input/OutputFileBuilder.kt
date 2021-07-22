package com.wcarmon.codegen.input

import com.wcarmon.codegen.model.Entity
import java.nio.file.Path

/**
 * Builds an appropriate output file from [Entity] & dest directory.
 * "Appropriate" depends on the template, target-language, etc
 */
fun interface OutputFileBuilder {

  /** Use the entity and dest dir to build a complete dest file */
  fun build(
    entity: Entity,
    outputDir: Path,
  ): Path
}
