package com.wcarmon.codegen.input

import com.wcarmon.codegen.model.Entity
import java.nio.file.Path

/**
 * Produces Entities from codegen entity config "files"
 *
 * See [com.wcarmon.codegen.PATTERN_FOR_ENTITY_FILE] for file name pattern
 */
fun interface EntityConfigParser {

  /**
   * @param entityConfigs input config "files" containing entities as json
   * @return parsed Entities
   */
  fun parse(entityConfigs: Collection<Path>): Collection<Entity>
}
