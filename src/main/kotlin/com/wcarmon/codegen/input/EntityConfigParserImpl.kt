package com.wcarmon.codegen.input

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.model.Entity
import java.nio.file.Path

class EntityConfigParserImpl(
  private val objectReader: ObjectReader,
) : EntityConfigParser {

  override fun parse(entityConfigs: Collection<Path>): Collection<Entity> {
    require(entityConfigs.isNotEmpty()) {
      "at least one entity config is required"
    }

    //TODO: Add linker for entity relationships

    return entityConfigs.map { parse(it) }
  }

  private fun parse(entityConfigFile: Path) =
    objectReader.readValue(
      entityConfigFile.toFile(),
      Entity::class.java)
}
