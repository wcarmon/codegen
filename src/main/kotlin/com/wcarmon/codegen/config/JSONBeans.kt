package com.wcarmon.codegen.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JSONBeans {

  @Bean
  fun objectMapper() = ObjectMapper()
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .registerModule(JavaTimeModule())
    .registerModule(KotlinModule())
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)!!
}
