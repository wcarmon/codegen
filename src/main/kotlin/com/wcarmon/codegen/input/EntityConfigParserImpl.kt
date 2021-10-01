package com.wcarmon.codegen.input

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.log.structuredDebug
import com.wcarmon.codegen.log.structuredError
import com.wcarmon.codegen.model.Entity
import org.apache.logging.log4j.LogManager
import java.nio.file.Path


class EntityConfigParserImpl(
  private val objectReader: ObjectReader,
) : EntityConfigParser {

  companion object {
    @JvmStatic
    private val LOG = LogManager.getLogger(EntityConfigParserImpl::class.java)
  }

  override fun parse(entityConfigs: Collection<Path>): Collection<Entity> {
    require(entityConfigs.isNotEmpty()) {
      "at least one entity config is required"
    }

    //TODO: Add linker for entity relationships

    return entityConfigs.map { path ->
      try {
        parse(path)

      } catch (ex: Exception) {
        LOG.structuredError(
          "Failed to parse entity",
          "cause" to ex,
          "path" to path,
        )

        throw RuntimeException("Failed to parse entity: path=$path", ex)
      }
    }.sortedBy { it.name.upperCamel }
  }

  private fun parse(entityConfigFile: Path): Entity {

    //TODO: do schema validation here
//    val schemaFactory: JsonSchemaFactory =
//      JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909)
//
//    val schema: JsonSchema = schemaFactory.getSchema(schemaStream)
//    val validationResult: Set<ValidationMessage> = schema.validate(json)

    LOG.structuredDebug(
      "Parsing file",
      "entityConfigFile" to entityConfigFile
    )

    return objectReader.readValue(
      entityConfigFile.toFile(),
      Entity::class.java
    )
  }
}
