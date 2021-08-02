package com.wcarmon.codegen

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.input.EntityConfigParser
import com.wcarmon.codegen.input.getPathsForNamePattern
import com.wcarmon.codegen.model.CodeGenRequest
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.OutputMode.MULTIPLE
import com.wcarmon.codegen.model.OutputMode.SINGLE
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.velocity.Template
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import java.nio.file.Files
import java.nio.file.Path

@Configuration
@EnableAutoConfiguration
class CodeGeneratorApp(
  private val entityConfigParser: EntityConfigParser,
  private val generator: CodeGenerator,
  private val objectReader: ObjectReader,
  private val templateLoader: (Path) -> Template,
) {

  companion object {
    @JvmStatic
    private val LOG = LogManager.getLogger(CodeGeneratorApp::class.java)
  }

  fun run(requestConfigRoot: Path) {
    require(Files.exists(requestConfigRoot)) { "configRoot must exist" }
    require(Files.isDirectory(requestConfigRoot)) { "configRoot must be a directory" }

    findCodeGenRequests(requestConfigRoot)
      .forEach { req ->

        val entities = findEntityConfigs(req.entityConfigDirs)

        LOG.info("Found entity configs for request: count={}, names=[{}]",
          entities.size,
          StringUtils.truncate(
            entities
              .map { it.name.upperCamel }
              .sortedBy { it }
              .joinToString(),
            256))

        // -- Enforce unique entity names
        val entityNames = entities.map { it.name }
        require(entityNames.size == entityNames.toSet().size) {
          "Entity names must be unique: entityNames=${entityNames.sortedBy { it.lowerCamel }}"
        }

        handleCodeGenRequest(req, entities)
      }
  }

  private fun handleCodeGenRequest(
    request: CodeGenRequest,
    entities: Collection<Entity>,
  ) {

    val template = templateLoader(request.template.file.toPath())

    when (request.outputMode) {
      SINGLE -> generator.generateToOneFile(
        entities = entities,
        request = request,
        template = template,
      )

      MULTIPLE -> {
        require(request.outputFilenameTemplate.isNotBlank()) {
          "outputFilenameTemplate required when generating multiple files"
        }

        //TODO: accept CaseFormat to support golang, c, rust, ...
        val fileNameBuilder = { entity: Entity ->
          String.format(
            request.outputFilenameTemplate,
            entity.name.upperCamel)
        }

        generator.generateToMultipleFiles(
          entities = entities,
          fileNameBuilder = fileNameBuilder,
          request = request,
          template = template,
        )
      }
    }
  }

  private fun findCodeGenRequests(configRoot: Path): Collection<CodeGenRequest> {

    val generatorRequestPaths = getPathsForNamePattern(
      pathPattern = PATTERN_FOR_GEN_REQ_FILE,
      searchRoot = configRoot,
    )

    require(generatorRequestPaths.isNotEmpty()) {
      "At least one CodeGen request file is required in $configRoot"
    }

    LOG.info("Found code gen requests: count={}, files={}",
      generatorRequestPaths.size,
      StringUtils.truncate(generatorRequestPaths.toString(), 256))

    return generatorRequestPaths.map {
      //TODO: validate via json-schema here
      objectReader.readValue(
        it.toFile(),
        CodeGenRequest::class.java)
    }
  }

  private fun findEntityConfigs(configRoots: Collection<Path>): Collection<Entity> {
    require(configRoots.isNotEmpty()) { "at least one configRoot is required" }

    val entityConfigPaths = configRoots.flatMap { configRoot ->
      getPathsForNamePattern(
        pathPattern = PATTERN_FOR_ENTITY_FILE,
        searchRoot = configRoot,
      )
    }.toSet()

    require(entityConfigPaths.isNotEmpty()) {
      "At least one entity config file is required in configRoots=$configRoots"
    }

    return entityConfigParser.parse(entityConfigPaths)
  }
}
