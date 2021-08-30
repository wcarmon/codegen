package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.google.common.base.CaseFormat
import com.wcarmon.codegen.TEMPLATE_SUFFIX
import com.wcarmon.codegen.model.OutputMode.FILE_PER_ENTITY
import com.wcarmon.codegen.model.OutputMode.SINGLE_FILE
import com.wcarmon.codegen.view.JVMRequestView
import com.wcarmon.codegen.view.Java8RequestView
import com.wcarmon.codegen.view.KotlinRequestView
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.PathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path


/**
 * Represents a parsed Code Generate request file
 *
 * See [com.wcarmon.codegen.PATTERN_FOR_GEN_REQ_FILE] for file name pattern
 *
 * See src/main/resources/json-schema/request.schema.json
 */
// `$id` and `$schema` are part of json standard, but not useful for code generation
@JsonIgnoreProperties("\u0024schema", "\u0024id")
@JsonPropertyOrder(alphabetic = true)
data class CodeGenRequest(
  val entityConfigDirs: Collection<Path>,

  private val outputFileOrDirectory: Path,

  /**
   * Prefixed with "classpath:" or "file:"
   *
   * eg. classpath:/templates/jdbc-template/row-mapper.vm
   * eg. file:///tmp/templates/row-mapper.vm
   */
  private val templateURI: URI,

  val allowOverride: Boolean = true,

  /**
   * fully qualified classes/enums/interfaces to import
   */
  val extraJVMImports: Set<String> = setOf(),

  val extraProtobufImports: Set<String> = setOf(),

  /**
   * Fully qualified Request context class
   * TODO: explain more here
   */
  val jvmContextClass: String = "",

  /**
   * Uses format of String.format
   * See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...)
   */
  val outputFilenameTemplate: String = "",

  val outputMode: OutputMode,

  @JsonProperty("package")
  val packageName: PackageName = PackageName.DEFAULT,

  /** Only used for OutputMode.MULTIPLE */
  val caseFormatForOutputFile: CaseFormat = CaseFormat.UPPER_CAMEL,
) {

  @JsonIgnore
  val cleanOutput: Path = outputFileOrDirectory.normalize().toAbsolutePath()

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
      require(filename.endsWith(TEMPLATE_SUFFIX)) { "template must end with $TEMPLATE_SUFFIX: $template" }
    }

    require(jvmContextClass.trim() == jvmContextClass) {
      "jvmContextClass must be trimmed: jvmContextClass='$jvmContextClass', this=$this"
    }

    //TODO: validate regex on extraJVMImports
    // See pattern in src/main/resources/json-schema/request.schema.json

    when (outputMode) {

      SINGLE_FILE -> {
        require(outputFilenameTemplate.isBlank()) {
          "outputFilenameTemplate is forbidden when generating single file"
        }

        if (Files.exists(cleanOutput)) {
          require(Files.isRegularFile(cleanOutput)) {
            "Either delete or put a regular file at $cleanOutput"
          }
        }
      }

      FILE_PER_ENTITY -> {
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

  val prettyTemplateName = when (template) {
    is ClassPathResource -> "classpath:${template.path}"
    is FileSystemResource -> template.uri.toString()
    else -> TODO("decide how to pretty-print this template")
  }

  val inDefaultPackage: Boolean = packageName == PackageName.DEFAULT

  val java8View by lazy {
    Java8RequestView(this)
  }

  val jvmView by lazy {
    JVMRequestView(this)
  }

  val kotlinView by lazy {
    KotlinRequestView(this)
  }
}
