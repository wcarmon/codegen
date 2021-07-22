@file:JvmName("CodegenMain")

package com.wcarmon.codegen

import com.wcarmon.codegen.config.CodegenBeans
import com.wcarmon.codegen.config.JSONBeans
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import java.nio.file.Paths

/**
 * Entry point
 *
 * TODO: document command line arg
 */
fun main(args: Array<String>) {
  val ctx = SpringApplicationBuilder(CodeGeneratorApp::class.java)
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

  //TODO: read from args
  val searchRoot = Paths.get(
    "/home/wcarmon/git-repos/modern-jvm/codegen/input")
    .normalize()
    .toAbsolutePath()

  ctx.getBean(CodeGeneratorApp::class.java)
    .run(searchRoot)
}
