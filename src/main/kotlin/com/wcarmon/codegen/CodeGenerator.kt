package com.wcarmon.codegen


import com.wcarmon.codegen.input.OutputFileBuilder
import com.wcarmon.codegen.model.Entity
import org.apache.logging.log4j.LogManager
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer


/** Generates code, given a template, entities and a destination */
class CodeGenerator(
  private val velocityEngine: VelocityEngine,
  private val onAfterGenerate: Consumer<Path> = Consumer { LOG.info("Generated: $it") },
) {

  companion object {
    @JvmStatic
    private val LOG = LogManager.getLogger(CodeGenerator::class.java)
  }

  /**
   * @param templateName  ...TODO ...
   */
  fun templateForPath(templateName: String): Template {
    require(templateName.isNotBlank()) { "Template name required" }

    return velocityEngine.getTemplate(templateName)
  }

  /**
   * Use when each entity goes into separate output file.
   *
   * Template should expect "entity" object in the velocity context
   */
  fun generateToMultipleFiles(
    entities: Collection<Entity>,
    fileNameBuilder: OutputFileBuilder,
    outputDir: Path,
    template: Template,
    allowOverwrite: Boolean = true,
  ) {
    require(entities.isNotEmpty()) { "no entities passed" }

    Files.createDirectories(outputDir)
    require(Files.isDirectory(outputDir)) { "Either delete or put a directory at $outputDir" }

    entities.forEach { entity ->

      val dest = fileNameBuilder.build(entity, outputDir)
        .normalize().toAbsolutePath()

      if (Files.exists(dest) && !allowOverwrite) {
        LOG.warn("Refusing to overwrite $dest")
        return@forEach
      }

      val context = VelocityContext()
      context.put("entity", entity)

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
    outputFile: Path,
    template: Template,
    allowOverwrite: Boolean = true,
  ) {
    require(entities.isNotEmpty()) { "no entities passed" }

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

    val dest = outputFile.normalize().toAbsolutePath()
    Files.newBufferedWriter(dest).use { writer ->
      template.merge(context, writer)
    }

    onAfterGenerate.accept(dest)
  }
}
