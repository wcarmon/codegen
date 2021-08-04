package com.wcarmon.codegen.config

import com.fasterxml.jackson.databind.ObjectReader
import com.wcarmon.codegen.CodeGenerator
import com.wcarmon.codegen.input.EntityConfigParser
import com.wcarmon.codegen.input.EntityConfigParserImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CodegenBeans {

  @Bean
  fun codeGenerator() = CodeGenerator()

  @Bean
  fun entityParser(
    objectReader: ObjectReader,
  ): EntityConfigParser =
    EntityConfigParserImpl(objectReader)
}
