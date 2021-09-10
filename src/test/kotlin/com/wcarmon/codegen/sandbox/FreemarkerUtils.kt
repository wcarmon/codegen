package com.wcarmon.codegen.sandbox

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Path

private val LOG = LogManager.getLogger("FreemarkerUtils")


fun generateFileFromTemplate(
  dataForTemplate: Map<String, Any>,
  dest: Path,
  template: Template,
) {
  check(!Files.exists(dest)) {
    "Failure: file already exists at $dest"
  }

  Files.newBufferedWriter(dest)
    .use { writer ->
      template.process(dataForTemplate, writer)
    }

  LOG.info("Wrote file: path=$dest")
}


fun getFreemarkerConfig() =
  Configuration(Configuration.VERSION_2_3_29)
    .also {
      it.defaultEncoding = "UTF-8"
      it.fallbackOnNullLoopVariable = false
      it.logTemplateExceptions = false
      it.templateExceptionHandler = TemplateExceptionHandler.DEBUG_HANDLER
      it.templateLoader = ClassTemplateLoader(GradleConfig::class.java, "/")
      it.whitespaceStripping = true
      it.wrapUncheckedExceptions = true
    }
