package com.wcarmon.codegen

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.input.EntityConfigParser
import com.wcarmon.codegen.input.OutputFileNameBuilder
import com.wcarmon.codegen.input.getPathsForNamePattern
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

    val requests = findCodeGenRequests(configRoot)
    val entities = findEntityConfigs(configRoot)

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

  private fun findCodeGenRequests(configRoot: Path): Collection<CodeGenRequest> {

    val generatorRequestPaths = getPathsForNamePattern(
      pathPattern = PATTERN_FOR_GEN_REQ_FILE,
      searchRoot = configRoot,
    )

    require(generatorRequestPaths.isNotEmpty()) {
      "At least one CodeGen request file is required in $configRoot"
    }

    LOG.info("Found code gen requests: count={}, files={}",
      generatorRequestPaths.size, generatorRequestPaths)

    return generatorRequestPaths.map {
      objectReader.readValue(
        it.toFile(),
        CodeGenRequest::class.java)
    }
  }

  private fun findEntityConfigs(configRoot: Path): Collection<Entity> {

    val entityConfigPaths = getPathsForNamePattern(
      pathPattern = PATTERN_FOR_ENTITY_FILE,
      searchRoot = configRoot,
    )

    require(entityConfigPaths.isNotEmpty()) {
      "At least one entity config file is required in $configRoot"
    }

    LOG.info("Found entity config files: count={}", entityConfigPaths.size)

    return entityConfigParser.parse(entityConfigPaths)
  }
}
