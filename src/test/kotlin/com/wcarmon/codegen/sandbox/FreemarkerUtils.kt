package com.wcarmon.codegen.sandbox

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler

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
