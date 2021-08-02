package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.TEMPLATE_SUFFIX
import com.wcarmon.codegen.model.OutputMode.MULTIPLE
import com.wcarmon.codegen.model.OutputMode.SINGLE
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.PathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path


/**
 * Represents a parsed code-gen request file
 *
 * See [com.wcarmon.codegen.PATTERN_FOR_GEN_REQ_FILE] for file name pattern
 *
 * See src/main/resources/json-schema/request.schema.json
 */
@JsonIgnoreProperties("\u0024schema", "\u0024id")
@JsonPropertyOrder(alphabetic = true)
data class CodeGenRequest(
  val entityConfigDirs: Collection<Path>,
  private val outputFileOrDirectory: Path,

  /**
   * Prefixed with "file:" or "classpath:"
   *
   * eg. classpath:/templates/jdbc-template/row-mapper.vm
   * eg. file:///tmp/templates/row-mapper.vm
   */
  private val templateURI: URI,

  val allowOverride: Boolean = true,

  /**
   * Uses format of String.format
   * See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...)
   */
  val outputFilenameTemplate: String = "",
  val outputMode: OutputMode,

  @JsonProperty("package")
  val packageName: PackageName? = null,
) {

  @JsonIgnore
  val cleanOutput = outputFileOrDirectory.normalize().toAbsolutePath()

  /**
   * eg. file: or classpath:
   * See https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#resources
   */
  val template: Resource

  init {
    require(entityConfigDirs.isNotEmpty()) { "At least one entity config file required" }
    entityConfigDirs.forEach {
      require(!Files.exists(it) || Files.isDirectory(it)) {
        "Expected directory at ${it.toAbsolutePath()}"
      }
    }

    require(entityConfigDirs.any { Files.exists(it) }) {
      "None of the entity directories exist: $entityConfigDirs"
    }

    val t = templateURI.toString()
    template =
      if (t.startsWith(CLASSPATH_URL_PREFIX)) {
        ClassPathResource(
          t.substringAfter(CLASSPATH_URL_PREFIX),
          CodeGenRequest::class.java.classLoader)

      } else if (templateURI.toURL().protocol == "file") {
        PathResource(templateURI)

      } else {
        TODO("Define resource for templateURI=$templateURI")
      }

    if (template.isFile) {
      require(template.exists()) { "cannot find template at ${template.uri}" }

      val filename =
        template.filename ?: throw IllegalArgumentException("template filename required")
      require(filename.endsWith(TEMPLATE_SUFFIX)) { "template must end with .vm: $template" }
    }

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

        require(outputFilenameTemplate.contains("%s")) {
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
