package com.wcarmon.codegen

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.input.EntityConfigParser
import com.wcarmon.codegen.input.OutputFileNameBuilder
import com.wcarmon.codegen.input.getFilesForNamePattern
import com.wcarmon.codegen.model.CodeGenRequest
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.OutputMode.MULTIPLE
import com.wcarmon.codegen.model.OutputMode.SINGLE
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
  private val templateBuilder: (Path) -> Template,
) {

  companion object {
    @JvmStatic
    private val LOG = LogManager.getLogger(CodeGeneratorApp::class.java)
  }

  fun run(configRoot: Path) {
    require(Files.exists(configRoot)) { "configRoot must exist" }
    require(Files.isDirectory(configRoot)) { "configRoot must be a directory" }

    val requests = findGenRequest(configRoot)

    val entities = findEntities(configRoot)

    requests.forEach {
      handleCodeGenRequest(it, entities)
    }
  }

  private fun handleCodeGenRequest(
    request: CodeGenRequest,
    entities: Collection<Entity>,
    fileNameBuilder: OutputFileNameBuilder? = null, //TODO pass as part of CodeGenRequest
  ) {

    when (request.outputMode) {
      SINGLE -> generator.generateToOneFile(
        allowOverwrite = request.allowOverride,
        entities = entities,
        outputFile = request.cleanOutput,
        template = templateBuilder(request.cleanTemplatePath),
      )

      MULTIPLE -> {
        requireNotNull(fileNameBuilder) { "fileNameBuilder required" }

        generator.generateToMultipleFiles(
          allowOverwrite = request.allowOverride,
          entities = entities,
          fileNameBuilder = fileNameBuilder,
          outputDir = request.cleanOutput,
          template = templateBuilder(request.cleanTemplatePath),
        )
      }
    }
  }

  private fun findGenRequest(configRoot: Path): Collection<CodeGenRequest> {

    val generatorRequestFiles = getFilesForNamePattern(
      filePattern = PATTERN_FOR_GEN_REQ_FILE,
      searchRoot = configRoot,
    )

    require(generatorRequestFiles.isNotEmpty()) {
      "At least one generator request file is required in $configRoot"
    }

    LOG.info("Found code gen requests: count={}, files={}",
      generatorRequestFiles.size, generatorRequestFiles)

    return generatorRequestFiles.map {
      objectReader.readValue(
        it.toFile(),
        CodeGenRequest::class.java)
    }
  }

  private fun findEntities(configRoot: Path): Collection<Entity> {

    val entityConfigFiles = getFilesForNamePattern(
      filePattern = PATTERN_FOR_ENTITY_FILE,
      searchRoot = configRoot,
    )

    require(entityConfigFiles.isNotEmpty()) {
      "At least one entity config file is required in $configRoot"
    }

    LOG.info("Found entity config files: count={}", entityConfigFiles.size)

    return entityConfigParser.parse(entityConfigFiles)
  }
}
