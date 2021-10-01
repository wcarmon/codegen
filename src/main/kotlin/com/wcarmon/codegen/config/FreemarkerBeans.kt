package com.wcarmon.codegen.config

import freemarker.cache.FileTemplateLoader
import freemarker.cache.MultiTemplateLoader
import freemarker.cache.TemplateLoader
import freemarker.template.Configuration.VERSION_2_3_29
import freemarker.template.TemplateExceptionHandler
import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths


private val LOG = LogManager.getLogger(FreemarkerBeans::class.java)

@Configuration
class FreemarkerBeans {

  @Bean
  fun templateLoader(): TemplateLoader {
    val templatesInUserHome = Paths.get(
      System.getProperty("user.home"), ".codegen", "templates"
    )

    // Spring does the class path -> Path resolution, so I don't need ClassTemplateLoader
    return MultiTemplateLoader(
      arrayOf(
        FileTemplateLoader(File("/")),
        //        FileTemplateLoader(templatesInUserHome.toFile()),
      )
    )
  }

  @Bean
  fun freemarkerConfig(templateLoader: TemplateLoader) =
    freemarker.template.Configuration(VERSION_2_3_29)
      .also {

        it.defaultEncoding = "UTF-8"
        it.fallbackOnNullLoopVariable = false
        it.logTemplateExceptions = true // shows real exception on failures
        it.templateExceptionHandler = TemplateExceptionHandler.DEBUG_HANDLER
        it.templateLoader = templateLoader
        it.whitespaceStripping = true
        it.wrapUncheckedExceptions = true // includes template line info
//        it.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
      }
//TODO: find equivalent for this
//      it.addProperty("directive.foreach.max_loops", "5000")
//      it.addProperty("runtime.log.log_invalid_method_calls", "true")
//      it.addProperty("runtime.log.log_invalid_references", "true")
//      it.addProperty("runtime.strict_mode.enable", "true")

  @Bean
  fun templateFinder(cfg: freemarker.template.Configuration) =
    { templatePath: Path ->
      cfg.getTemplate(templatePath.toAbsolutePath().toString())
    }
}
