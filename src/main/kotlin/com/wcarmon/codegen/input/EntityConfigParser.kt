package com.wcarmon.codegen.input

import com.wcarmon.codegen.model.Entity
import java.nio.file.Path

/** Produces Entities from codegen entity config "files" */
fun interface EntityConfigParser {

  /**
   * @param entityConfigs input config "files" containing entities as json
   * @return parsed Entities
   */
  fun parse(entityConfigs: Collection<Path>): Collection<Entity>
}
