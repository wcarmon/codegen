package com.wcarmon.codegen.config

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.CodeGenerator
import com.wcarmon.codegen.TEMPLATE_SUFFIX
import com.wcarmon.codegen.input.EntityConfigParser
import com.wcarmon.codegen.input.EntityConfigParserImpl
import org.apache.logging.log4j.LogManager
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.apache.velocity.runtime.resource.loader.FileResourceLoader
import org.apache.velocity.runtime.resource.loader.URLResourceLoader
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
  fun entityParser(
    objectReader: ObjectReader,
  ): EntityConfigParser =
    EntityConfigParserImpl(objectReader)

  @Bean
  fun velocityEngine() = VelocityEngine()
    .also {
      it.addProperty("runtime.log.log_invalid_references", "true")  // expensive

      // GOTCHA: names are arbitrary
//      it.addProperty(VelocityEngine.RESOURCE_LOADERS, "classpath,file,url")
      it.addProperty(VelocityEngine.RESOURCE_LOADERS, "url")
      it.addProperty("resource.manager.log_when_found", "true")

      it.addProperty("resource.loader.classpath.class", ClasspathResourceLoader::class.java.name)

      it.addProperty("resource.loader.file.class", FileResourceLoader::class.java.name)
      it.addProperty("resource.loader.file.path", extraTemplatesPath.toString())

      it.addProperty("resource.loader.url.class", URLResourceLoader::class.java.name)
      it.addProperty("resource.loader.url.root", "file://") // local URIs
      it.addProperty("resource.loader.url.timeout", "10000") // milliseconds

      it.init()

      LOG.info("extra templates can go at $extraTemplatesPath/*${TEMPLATE_SUFFIX}")
    }

  @Bean
  fun templateBuilder(velocityEngine: VelocityEngine) =
    { templatePath: Path ->
      velocityEngine.getTemplate(templatePath.toAbsolutePath().toString())
    }
}
