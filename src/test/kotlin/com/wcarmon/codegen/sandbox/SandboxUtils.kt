package com.wcarmon.codegen.sandbox

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom

//TODO: delete me
fun main() {

  val gradleConfig = DEFAULT_GRADLE_CONFIG.copy(
    projectName = "sandbox-" + ThreadLocalRandom.current().nextInt(100)
  )

  buildGradleSandbox(gradleConfig)

  gradleTest(gradleConfig,
    maxWait = Duration.ofSeconds(30))

  gradleRun(gradleConfig,
    maxWait = Duration.ofSeconds(30))
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
