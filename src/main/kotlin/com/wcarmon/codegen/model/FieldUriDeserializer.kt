package com.wcarmon.codegen.model

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.net.URL

/**
 * Parse each FieldURI into [Field]
 */
class FieldUriDeserializer : JsonDeserializer<List<Field>>() {

  override fun deserialize(
    parser: JsonParser,
    context: DeserializationContext,
  ): List<Field> {

    val jsonFactory = JsonFactory(parser.codec)

    return context.readValue(parser, Collection::class.java)
      .map { path ->
        jsonFactory
          .createParser(URL(path as String))
          .use { parser ->
            parser.readValueAs(Field::class.java)
          }
      }
  }
}
