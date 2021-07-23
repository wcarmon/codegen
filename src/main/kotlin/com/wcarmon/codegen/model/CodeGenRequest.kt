package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.TEMPLATE_SUFFIX
import com.wcarmon.codegen.model.OutputMode.MULTIPLE
import com.wcarmon.codegen.model.OutputMode.SINGLE
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name


/**
 * Represents a parsed code-gen request file
 *
 * See [com.wcarmon.codegen.PATTERN_FOR_GEN_REQ_FILE] for file name pattern
 */
@JsonPropertyOrder(alphabetic = true)
data class CodeGenRequest(
  val entityConfigPaths: Collection<Path>,
  private val outputFileOrDirectory: Path,
  private val templatePath: Path,
  val allowOverride: Boolean = true,

  /**
   * Uses format of String.format
   * See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...)
   */
  val outputFilenameTemplate: String = "",
  val outputMode: OutputMode,
) {

  @JsonIgnore
  val cleanOutput = outputFileOrDirectory.normalize().toAbsolutePath()

  @JsonIgnore
  val cleanTemplatePath = templatePath.normalize().toAbsolutePath()

  init {
    require(entityConfigPaths.isNotEmpty()) { "At least one entity config file required" }

    require(Files.exists(templatePath)) { "cannot find template at $templatePath" }
    require(Files.isRegularFile(templatePath)) { "template file required at $templatePath" }
    require(templatePath.name.endsWith(TEMPLATE_SUFFIX)) { "template must end with .vm: $templatePath" }

    when (outputMode) {

      SINGLE -> {
        require(outputFilenameTemplate.isBlank()) {
          "outputFilenameTemplate is forbidden when generating single file"
        }

        if (Files.exists(cleanOutput)) {
          require(Files.isRegularFile(cleanOutput)) {
            "Either delete or put a regular file at $cleanOutput"
          }
        }
      }

      MULTIPLE -> {
        require(outputFilenameTemplate.isNotBlank()) {
          "outputFilenameTemplate required when generating multiple files"
        }

        require(outputFilenameTemplate.contains("%s")){
          "outputFilenameTemplate must contain a placeholder for entity name: $outputFilenameTemplate"
        }

        if (Files.exists(cleanOutput)) {
          require(Files.isDirectory(cleanOutput)) {
            "Either delete or put a directory at $cleanOutput"
          }
        }
      }
    }
  }
}
