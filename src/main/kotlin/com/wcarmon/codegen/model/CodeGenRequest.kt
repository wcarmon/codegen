package com.wcarmon.codegen.model

import com.wcarmon.codegen.model.OutputMode.MULTIPLE
import com.wcarmon.codegen.model.OutputMode.SINGLE
import java.nio.file.Files
import java.nio.file.Path


/**
 * Represents a parsed code-gen request file
 *
 * See [com.wcarmon.codegen.PATTERN_FOR_GEN_REQ_FILE] for file name pattern
 */
data class CodeGenRequest(
  val entityConfigPaths: Collection<Path>,
  val outputMode: OutputMode,
  private val outputFileOrDirectory: Path,
  private val templatePath: Path,
) {

  val cleanOutput = outputFileOrDirectory.normalize().toAbsolutePath()
  val cleanTemplate = templatePath.normalize().toAbsolutePath()

  init {
    require(entityConfigPaths.isNotEmpty()) { "At least one entity config file required" }

    require(Files.exists(templatePath)) { "cannot find template at $templatePath" }
    require(Files.isRegularFile(templatePath)) { "template file required at $templatePath" }
    require(templatePath.endsWith(".vm")) { "template must end with .vm: $templatePath" }

    when (outputMode) {

      SINGLE ->
        if (Files.exists(cleanOutput)) {
          require(Files.isRegularFile(cleanOutput)) {
            "Either delete or put a regular file at $cleanOutput"
          }
        }

      MULTIPLE ->
        if (Files.exists(cleanOutput)) {
          require(Files.isDirectory(cleanOutput)) {
            "Either delete or put a directory at $cleanOutput"
          }
        }
    }
  }
}
