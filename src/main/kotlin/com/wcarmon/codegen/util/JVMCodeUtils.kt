@file:JvmName("JVMCodeUtils")

/** Utilities common to all JVM languages */
package com.wcarmon.codegen.util

import com.google.common.base.Preconditions.checkState
import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.QuoteType.*
import com.wcarmon.codegen.model.Serde
import com.wcarmon.codegen.model.StringFormatTemplate
import com.wcarmon.codegen.model.TargetLanguage.JAVA_08

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
@Suppress("ReturnCount")
fun defaultValueLiteralForJVM(field: Field): String {

  checkState(field.defaultValue.isPresent) {
    "Method only applicable when default value present: field=$field"
  }

  if (field.defaultValue.isNullLiteral) {
    return "null"
  }

  return quoteTypeForJVMLiterals(field.type.base)
    .wrap(field.defaultValue.literal.toString())
}

//TODO: document me
fun defaultJVMSerde(field: Field): Serde =
  Serde(
    deserializeTemplate = defaultJVMDeserializeTemplate(field),
    serializeTemplate = defaultJVMSerializeTemplate(field),
  )


/**
 * Dedupes, Sorts, trims, flattens (Iterables)
 *
 * @param importables: zero or more [Iterable<String>] or [String]
 */
fun consolidateImports(
  importables: Iterable<Any>,
): Collection<String> {

  // -- Validate
  importables.forEachIndexed { index, arg ->
    require((arg is String) || (arg is Iterable<*>)) {
      "argument must be String or Iterable<String>: index=$index, arg=$arg"
    }
  }

  // -- Clean
  val output = mutableSetOf<String>()

  importables
    .filterIsInstance<String>()
    .map { it.trim() }
    .filter { it.isNotBlank() }
    .forEach(output::add)

  importables
    .filterIsInstance<Iterable<*>>()
    .flatten()
    .map { (it as String).trim() }
    .filter { it.isNotBlank() }
    .forEach(output::add)

  return output.toSortedSet()
}


//TODO: document me
private fun defaultJVMSerializeTemplate(field: Field) =
  when (field.effectiveBaseType(JAVA_08)) {

    // TODO: JSON serialized via Jackson
    ARRAY,
    LIST,
    MAP,
    SET,
    -> TODO("fix jackson serializer for field=$field")

    //TODO: more branches here
    else -> StringFormatTemplate("%s.toString()")
  }


/**
 * Deserializer: Converts from String to the type
 *
 * See [com.wcarmon.codegen.model.Serde]
 *
 * @returns jvm expression, uses %s as placeholder for field value
 */
private fun defaultJVMDeserializeTemplate(field: Field) =
  StringFormatTemplate(
    when (field.effectiveBaseType(JAVA_08)) {

      INT_16 -> "Short.parseShort(%s)"
      INT_32 -> "Integer.parseInt(%s)"
      INT_64 -> "Long.parseLong(%s)"
      INT_8 -> "Byte.parseByte(%s)"
      PATH -> "${javaTypeLiteral(field)}.of(%s)"
      STRING -> "String.valueOf(%s)"
      URI -> "${javaTypeLiteral(field)}.create(%s)"
      URL -> "new ${javaTypeLiteral(field)}(%s)"
      UUID -> "${javaTypeLiteral(field)}.fromString(%s)"

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
      -> "${javaTypeLiteral(field)}.parse(%s)"

      // TODO: JSON serialized via Jackson?
      ARRAY,
      LIST,
      MAP,
      SET,
      -> TODO("Fix string deserializer for field=$field")

      else -> "%s"
    })
