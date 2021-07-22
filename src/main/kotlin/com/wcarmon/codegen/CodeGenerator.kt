package com.wcarmon.codegen


import com.wcarmon.codegen.input.OutputFileBuilder
import com.wcarmon.codegen.model.Entity
import org.apache.logging.log4j.LogManager
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.File
import java.io.FileWriter
import java.util.function.Consumer


/** Generates code given a template, entities and a destination */
class CodeGenerator(
  private val onAfterGenerate: Consumer<File> = Consumer { LOG.info("Generated: $it") },
  private val velocityEngine: VelocityEngine,
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
   * Use when each entity goes into separate output file
   * Template should expect "entity" object in the context
   */
  fun generateToMultipleFiles(
    entities: Collection<Entity>,
    fileNameBuilder: OutputFileBuilder,
    outputDir: File,
    template: Template,
    allowOverwrite: Boolean = true,
  ) {
    check(entities.isNotEmpty()) { "no entities passed" }

    outputDir.mkdirs()
    require(outputDir.isDirectory) { "Either delete or put a directory at $outputDir" }

    entities.forEach { entity ->
      val context = VelocityContext()
      context.put("entity", entity)

      val dest = fileNameBuilder.build(entity, outputDir).canonicalFile.absoluteFile
      if (dest.exists() && !allowOverwrite) {
        LOG.warn("Refusing to overwrite $dest")
        return@forEach
      }

      FileWriter(dest).use {
        template.merge(context, it)
      }

      onAfterGenerate.accept(dest)
    }
  }

  /**
   * Use when all entities go into the same output file
   * Template should expect "entities" collection in the context
   *
   * This case is less common
   */
  fun generateToOneFile(
    entities: Collection<Entity>,
    outputFile: File,
    template: Template,
    allowOverwrite: Boolean = true,
  ) {
    check(entities.isNotEmpty()) { "no entities passed" }

    outputFile.parentFile.mkdirs()

    if (outputFile.exists()) {
      if (!allowOverwrite) {
        LOG.warn("Refusing to overwrite $outputFile")
        return
      }

      require(outputFile.isFile) { "Either delete or put a regular file at $outputFile" }
    }

    val context = VelocityContext()
    context.put("entities", entities)

    val dest = outputFile.canonicalFile.absoluteFile
    FileWriter(dest).use {
      template.merge(context, it)
    }

    onAfterGenerate.accept(dest)
  }
}
