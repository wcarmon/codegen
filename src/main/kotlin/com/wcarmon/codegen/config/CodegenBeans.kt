package com.wcarmon.codegen.config

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.CodeGenerator
import com.wcarmon.codegen.input.EntityConfigParser
import com.wcarmon.codegen.input.EntityConfigParserImpl
import org.apache.logging.log4j.LogManager
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.apache.velocity.runtime.resource.loader.FileResourceLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolute

private val LOG = LogManager.getLogger(CodegenBeans::class.java)

@Configuration
class CodegenBeans {

  private val extraTemplatesPath = Paths.get(
    System.getProperty("user.home"),
    ".codegen/templates",
  ).normalize().absolute()

  @Bean
  fun codeGenerator() = CodeGenerator()

  @Bean
  fun velocityEngine() = VelocityEngine()
    .also {
      it.addProperty("runtime.log.invalid.references", "true")  // expensive

      it.addProperty("resource.manager.logwhenfound", "true")
      it.addProperty("resource.loader", "file,classpath") // GOTCHA: names are arbitrary

      it.addProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
      it.addProperty("file.resource.loader.class", FileResourceLoader::class.java.name)
      it.addProperty("file.resource.loader.path", extraTemplatesPath.toString())
      it.init()

      LOG.info("extra templates can go at $extraTemplatesPath/*.vm")
    }

  @Bean
  fun entityParser(
    objectReader: ObjectReader,
  ): EntityConfigParser =
    EntityConfigParserImpl(objectReader)

  @Bean
  fun templateBuilder(velocityEngine: VelocityEngine) =
    { templatePath: Path ->
      velocityEngine.getTemplate(
        templatePath.toAbsolutePath().toString())
    }
}
