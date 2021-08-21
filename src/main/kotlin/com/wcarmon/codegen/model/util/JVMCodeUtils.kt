@file:JvmName("JVMCodeUtils")

/** Utilities common to all JVM languages */
package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.QuoteType.*

/**
 * Output only applicable to JVM languages (eg. Java, Kotlin, groovy...)
 *
 * @return Quote type for the logical base type
 */
fun quoteTypeForJVMLiterals(base: BaseFieldType) = when (base) {

  CHAR -> SINGLE

  BOOLEAN,
  FLOAT_32,
  FLOAT_64,
  INT_128,
  INT_16,
  INT_32,
  INT_64,
  INT_8,
  YEAR,
  ZONE_OFFSET,
  -> NONE

  FLOAT_BIG,
  INT_BIG,
  -> TODO("Determine quote type for JVM literal: $base")

  else -> DOUBLE
}

/**
 * Output only applicable to JVM languages (eg. Java, Kotlin, groovy...)
 *
 * @return the default value literal
 */
fun defaultValueLiteralForJVM(field: Field): String? {
  if (field.defaultValue == null) {
    return null
  }

  if (field.shouldDefaultToNull) {
    return "null"
  }

  return quoteTypeForJVMLiterals(field.type.base)
    .wrap(field.defaultValue)
}

//TODO: document me
fun defaultJVMSerde(field: Field): Serde =
  Serde(
    deserializeTemplate = defaultJVMDeserializeTemplate(field.type),
    serializeTemplate = defaultJVMSerializeTemplate(field.type),
  )


//TODO: document me
private fun defaultJVMSerializeTemplate(type: LogicalFieldType) = when (type.base) {

  // TODO: JSON serialized via Jackson
  ARRAY,
  LIST,
  MAP,
  SET,
  -> TODO("fix jackson serializer for $type")

  //TODO: more branches here
  else -> ExpressionTemplate("%s.toString()")
}


/**
 * Deserializer: Converts from String to the type
 *
 * See [com.wcarmon.codegen.model.Serde]
 *
 * @returns jvm expression, uses %s as placeholder for field value
 */
private fun defaultJVMDeserializeTemplate(type: LogicalFieldType) =
  ExpressionTemplate(
    when (type.base) {

      INT_16 -> "Short.parseShort(%s)"
      INT_32 -> "Integer.parseInt(%s)"
      INT_64 -> "Long.parseLong(%s)"
      INT_8 -> "Byte.parseByte(%s)"
      PATH -> "${javaTypeLiteral(type)}.of(%s)"
      STRING -> "String.valueOf(%s)"
      URI -> "${javaTypeLiteral(type)}.create(%s)"
      URL -> "new ${javaTypeLiteral(type)}(%s)"
      UUID -> "${javaTypeLiteral(type)}.fromString(%s)"

      DURATION,
      MONTH_DAY,
      PERIOD,
      USER_DEFINED,
      UTC_INSTANT,
      UTC_TIME,
      YEAR,
      YEAR_MONTH,
      ZONE_AGNOSTIC_DATE,
      ZONE_AGNOSTIC_TIME,
      ZONED_DATE_TIME,
      -> "${javaTypeLiteral(type)}.parse(%s)"

      // TODO: JSON serialized via Jackson
      ARRAY,
      LIST,
      MAP,
      SET,
      -> TODO("fix jackson string deserializer for $type")

      else -> "%s"
    })
