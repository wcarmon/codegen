package com.wcarmon.codegen


import com.wcarmon.codegen.input.OutputFileNameBuilder
import com.wcarmon.codegen.model.CodeGenRequest
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.util.DISTINCT_PROTO_COLLECTION_FIELDS_METHOD
import org.apache.logging.log4j.LogManager
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer


/** Generates code, given a template, entities and output destination */
class CodeGenerator(

  /** Executed after each file generated */
  private val onAfterGenerateFile: Consumer<Path> =
    Consumer { LOG.info("Generated: $it") },
) {

  companion object {
    @JvmStatic
    private val LOG = LogManager.getLogger(CodeGenerator::class.java)
  }

  /**
   * Use case #1:
   * Use when each entity goes into separate output file.
   *
   * Template should expect "entity" object available
   *
   * See also [generateOneFileForEntities]
   */
  fun generateFilePerEntity(
    entities: Collection<Entity>,
    fileNameBuilder: OutputFileNameBuilder,
    request: CodeGenRequest,
    template: freemarker.template.Template,
    allowOverwrite: Boolean = true,
  ) {
    require(entities.isNotEmpty()) { "no entities passed" }

    val outputDir = request.cleanOutput

    Files.createDirectories(outputDir)
    require(Files.isDirectory(outputDir)) {
      "Either delete or put a directory at $outputDir"
    }

    entities.forEach { entity ->

      val dest = Paths.get(
        outputDir.toString(),
        fileNameBuilder.build(entity),
      )

      if (Files.exists(dest) && !allowOverwrite) {
        LOG.warn("Refusing to overwrite $dest")
        return@forEach
      }

      val dataForTemplate = mapOf(
        "entity" to entity,
        "request" to request
      )

      Files.newBufferedWriter(dest).use { writer ->
        mergeAndStream(
          dataForTemplate = dataForTemplate,
          template = template,
          writer = writer,
        )
      }

      onAfterGenerateFile.accept(dest)
    }
  }

  /**
   * Use case #2:
   * Use when all entities go into the same output file
   *
   * Template should expect "entities" collection in the context
   *
   * This use case is less common than [generateFilePerEntity]
   */
  fun generateOneFileForEntities(
    entities: Collection<Entity>,
    request: CodeGenRequest,
    template: freemarker.template.Template,
    allowOverwrite: Boolean = true,
  ) {
    require(entities.isNotEmpty()) { "no entities passed" }

    val outputFile = request.cleanOutput

    Files.createDirectories(outputFile.parent)

    if (Files.exists(outputFile)) {
      if (!allowOverwrite) {
        LOG.warn("Refusing to overwrite $outputFile")
        return
      }

      require(Files.isRegularFile(outputFile)) {
        "Either delete or put a regular file at $outputFile"
      }
    }

    val dataForTemplate = mapOf(
      "entities" to entities,
      "getDistinctProtoCollectionFields" to DISTINCT_PROTO_COLLECTION_FIELDS_METHOD,
      "request" to request,
    )

    Files.newBufferedWriter(outputFile).use { writer ->
      mergeAndStream(
        dataForTemplate = dataForTemplate,
        template = template,
        writer = writer,
      )
    }

    onAfterGenerateFile.accept(outputFile)
  }

  private fun mergeAndStream(
    dataForTemplate: Map<String, Any>,
    template: freemarker.template.Template,
    writer: Writer,
  ) = template.process(dataForTemplate, writer)
}
