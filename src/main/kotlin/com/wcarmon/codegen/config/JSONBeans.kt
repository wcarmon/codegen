package com.wcarmon.codegen.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JSONBeans {

  @Bean
  fun objectMapper() = ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .registerModule(JavaTimeModule())
    .registerModule(
      KotlinModule(
        nullToEmptyCollection = true,
        nullToEmptyMap = true,
        // use my default values when null in json
        nullIsSameAsDefault = true,
      )
    )
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)!!

  @Bean
  fun objectReader(objectMapper: ObjectMapper): ObjectReader = objectMapper.reader()

  @Bean
  fun objectWriter(objectMapper: ObjectMapper): ObjectWriter =
    objectMapper.writerWithDefaultPrettyPrinter()
}
