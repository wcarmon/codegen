package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.google.common.base.CaseFormat
import com.wcarmon.codegen.DEBUG_MODE
import com.wcarmon.codegen.TEMPLATE_SUFFIX
import com.wcarmon.codegen.model.OutputMode.FILE_PER_ENTITY
import com.wcarmon.codegen.model.OutputMode.SINGLE_FILE
import com.wcarmon.codegen.model.TargetLanguage.JAVA_08
import com.wcarmon.codegen.view.GolangRequestView
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

  private val outputDirectory: Path,

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

  val extraGolangImports: Set<String> = setOf(),

  val extraProtobufImports: Set<String> = setOf(),

  /**
   * Fully qualified Request context class
   * TODO: explain more here
   */
  val jvmContextClass: String = "",

  /**
   * Uses format of String.format (for outputMode=multiple)
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
  val cleanOutputDir: Path = outputDirectory.normalize().toAbsolutePath()

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
          CodeGenRequest::class.java.classLoader
        )

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

    if (Files.exists(cleanOutputDir)) {
      require(Files.isDirectory(cleanOutputDir)) {
        "A regular directory is required at $cleanOutputDir"
      }
    }

    // -- Validate: outputFilenameTemplate
    require(outputFilenameTemplate.isNotBlank()) {
      "outputFilenameTemplate is required and blank"
    }

    val fileNameContainsPlaceholder = outputFilenameTemplate.contains("%s")
    when (outputMode) {
      SINGLE_FILE ->
        require(!fileNameContainsPlaceholder) {
          "outputFilenameTemplate must not contain a placeholder, since outputMode=single: $outputFilenameTemplate"
        }

      FILE_PER_ENTITY ->
        require(fileNameContainsPlaceholder) {
          "outputFilenameTemplate must contain a placeholder for entity name: $outputFilenameTemplate"
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
    Java8RequestView(
      debugMode = DEBUG_MODE,
      request = this,
    )
  }

  val jvmView by lazy {
    JVMRequestView(
      debugMode = DEBUG_MODE,
      request = this,
    )
  }

  val kotlinView by lazy {
    KotlinRequestView(
      debugMode = DEBUG_MODE,
      request = this,
    )
  }

  val golangView by lazy {
    GolangRequestView(
      debugMode = DEBUG_MODE,
      request = this,
    )
  }

  /**
   * @return CollectionFields, with the owning Entity
   */
  fun getCollectionFields(entities: Collection<Entity>): Collection<FieldAndOwner> =
    entities
      .flatMap { entity ->

        // -- Pair each field with the owning entity
        entity.fields.map { field ->
          FieldAndOwner(field = field, owner = entity)
        }
      }

      // -- Only retain collection fields
      .filter { pair ->
        //TODO: confim this is fine for golang, proto, ...
        pair.field.effectiveBaseType(JAVA_08).isCollection
      }
      .sortedBy { pair ->
        pair.field.name.lowerCamel
      }
}
