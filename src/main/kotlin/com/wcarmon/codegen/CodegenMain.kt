@file:JvmName("CodegenMain")

package com.wcarmon.codegen

import com.wcarmon.codegen.config.JSONBeans
import com.wcarmon.codegen.config.CodegenBeans
import com.wcarmon.codegen.input.EntityFileParser
import com.wcarmon.codegen.input.getEntityFiles
import com.wcarmon.codegen.model.OutputMode
import org.apache.velocity.Template
import org.apache.velocity.app.VelocityEngine
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Paths


@Configuration
@EnableAutoConfiguration
class CodeGeneratorApp

fun main(args: Array<String>) {
  val ctx = SpringApplicationBuilder(CodegenApp::class.java)
    .bannerMode(Banner.Mode.OFF)
    .headless(true)
    .logStartupInfo(true)
    .profiles("default")
    .web(WebApplicationType.NONE)
    .sources(
      JSONBeans::class.java,
      CodegenBeans::class.java,
    )
    .build()
    .run(*args)


  val searchRoot = File("/home/wcarmon/git-repos/modern-jvm/codegen/input")
  val outputMode = OutputMode.MULTIPLE

  // Relative to classpath root or to ~/.codegen/templates
  val templatePath = "/templates/kotlin.data-class.vm"
  val outputFile =
    Paths.get(System.getProperty("user.home"), "tmp/codegen/example.out").toFile()
  val outputDir =
    Paths.get(System.getProperty("user.home"), "tmp/codegen/example-dir").toFile()

  val entityFilePattern = "glob:**/*.entity.json"

  // -------------------------------------------------------------
  val entityConfigFiles = getEntityFiles(
    filePattern = entityFilePattern,
    searchRoot = searchRoot,
  )

  val generator = ctx.getBean(CodeGenerator::class.java)
  val entities = ctx.getBean(EntityFileParser::class.java)
    .parse(entityConfigFiles)

//  generate(
//    entities = entities,
//    outputMode = OutputMode.MULTIPLE,
//    template = vEngine.getTemplate(templatePath),
//  )
}
