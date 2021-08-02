package com.wcarmon.codegen


import com.wcarmon.codegen.input.OutputFileNameBuilder
import com.wcarmon.codegen.model.CodeGenRequest
import com.wcarmon.codegen.model.Entity
import org.apache.logging.log4j.LogManager
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer


/** Generates code, given a template, entities and a destination */
class CodeGenerator(
  private val onAfterGenerate: Consumer<Path> = Consumer { LOG.info("Generated: $it") },
) {

  companion object {
    @JvmStatic
    private val LOG = LogManager.getLogger(CodeGenerator::class.java)
  }

  /**
   * Use when each entity goes into separate output file.
   *
   * Template should expect "entity" object in the velocity context
   */
  fun generateToMultipleFiles(
    entities: Collection<Entity>,
    fileNameBuilder: OutputFileNameBuilder,
    request: CodeGenRequest,
    template: Template,
    allowOverwrite: Boolean = true,
  ) {
    require(entities.isNotEmpty()) { "no entities passed" }

    val outputDir = request.cleanOutput

    Files.createDirectories(outputDir)
    require(Files.isDirectory(outputDir)) { "Either delete or put a directory at $outputDir" }

    entities.forEach { entity ->

      val dest = Paths.get(
        outputDir.normalize().toAbsolutePath().toString(),
        fileNameBuilder.build(entity),
      )

      if (Files.exists(dest) && !allowOverwrite) {
        LOG.warn("Refusing to overwrite $dest")
        return@forEach
      }

      val context = VelocityContext()
      context.put("entity", entity)
      context.put("request", request)

      Files.newBufferedWriter(dest).use { writer ->
        template.merge(context, writer)
      }

      onAfterGenerate.accept(dest)
    }
  }

  /**
   * Use when all entities go into the same output file
   * Template should expect "entities" collection in the context
   *
   * This case is less common than [generateToMultipleFiles]
   */
  fun generateToOneFile(
    entities: Collection<Entity>,
    request: CodeGenRequest,
    template: Template,
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

    val context = VelocityContext()
    context.put("entities", entities)
    context.put("request", request)

    val dest = outputFile.normalize().toAbsolutePath()
    Files.newBufferedWriter(dest).use { writer ->
      template.merge(context, writer)
    }

    onAfterGenerate.accept(dest)
  }
}
