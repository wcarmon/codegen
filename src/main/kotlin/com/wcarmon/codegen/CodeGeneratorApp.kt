package com.wcarmon.codegen

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.input.EntityConfigParser
import com.wcarmon.codegen.input.getPathsMatchingNamePattern
import com.wcarmon.codegen.model.CodeGenRequest
import com.wcarmon.codegen.model.Entity
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

  /**
   * @param requestConfigRoot where to start looking for request.json files
   *
   * See [PATTERN_FOR_GEN_REQ_FILE]
   */
  fun run(requestConfigRoot: Path) {
    val cleanRoot = requestConfigRoot.normalize().toAbsolutePath()

    require(Files.exists(cleanRoot)) { "configRoot must exist at $cleanRoot" }
    require(Files.isDirectory(cleanRoot)) { "expected directory at $cleanRoot" }

    findCodeGenRequests(cleanRoot)
      .forEach { codeGenRequest ->

        val entities = findEntityConfigs(codeGenRequest.entityConfigDirs)

        LOG.info("Found entity configs for request: count={}, names=[{}]",
          entities.size,
          StringUtils.truncate(
            entities
              .map { it.name.upperCamel }
              .sortedBy { it }
              .joinToString(),
            256))

        // -- Enforce unique entity names
        val entityNames = entities.map { it.name.lowerCamel }
        require(entityNames.size == entityNames.toSet().size) {
          "Entity names must be unique: entityNames=${entityNames.sortedBy { it }}"
        }

        handleCodeGenRequest(codeGenRequest, entities)
      }
  }

  private fun handleCodeGenRequest(
    request: CodeGenRequest,
    entities: Collection<Entity>,
  ) {

    val template = templateLoader(request.template.file.toPath())

    if (request.outputMode == SINGLE) {
      generator.generateOneFileForEntities(
        entities = entities,
        request = request,
        template = template,
      )

      return
    }

    // Invariant: Generating multiple files

    require(request.outputFilenameTemplate.isNotBlank()) {
      "outputFilenameTemplate required when generating multiple files"
    }

    val fileNameBuilder = { entity: Entity ->
      val entityNameInFile = entity.name.forCaseFormat(
        request.caseFormatForOutputFile)

      String.format(
        request.outputFilenameTemplate,
        entityNameInFile)
    }

    generator.generateFilePerEntity(
      entities = entities,
      fileNameBuilder = fileNameBuilder,
      request = request,
      template = template,
    )
  }

  /**
   * Traverse `configRoot`,
   * parse found json files to [CodeGenRequest] instances
   *
   * @param configRoot file system root for searching
   * @return parsed [CodeGenRequest] instances
   *
   * See [PATTERN_FOR_GEN_REQ_FILE]
   */
  private fun findCodeGenRequests(configRoot: Path): Collection<CodeGenRequest> {

    val generatorRequestPaths = getPathsMatchingNamePattern(
      pathPattern = PATTERN_FOR_GEN_REQ_FILE,
      searchRoot = configRoot,
    )

    require(generatorRequestPaths.isNotEmpty()) {
      "At least one Code Generate request file is required under $configRoot"
    }

    LOG.info("Found Code Generate requests: count={}, files={}",
      generatorRequestPaths.size,
      StringUtils.truncate(generatorRequestPaths.toString(), 256))

    return generatorRequestPaths.map {
      //TODO: validate via json-schema here
      objectReader.readValue(
        it.toFile(),
        CodeGenRequest::class.java)
    }
  }

  /**
   * Traverse each root in `configRoots`,
   * parse found json files to [Entity] instances
   *
   * @param configRoots file system roots for searching
   * @return parsed [Entity] instances
   *
   * See [PATTERN_FOR_ENTITY_FILE]
   */
  private fun findEntityConfigs(configRoots: Collection<Path>): Collection<Entity> {
    require(configRoots.isNotEmpty()) { "At least one configRoot is required" }

    val entityConfigPaths = configRoots.flatMap { configRoot ->
      getPathsMatchingNamePattern(
        pathPattern = PATTERN_FOR_ENTITY_FILE,
        searchRoot = configRoot,
      )
    }.toSet()

    require(entityConfigPaths.isNotEmpty()) {
      "At least one entity config file is required under configRoots=$configRoots"
    }

    return entityConfigParser.parse(entityConfigPaths)
  }
}
