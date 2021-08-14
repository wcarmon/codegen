@file:JvmName("KotlinCodeUtils")

/** Utilities only useful for generating Kotlin */
package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType

fun getKotlinTypeLiteral(
  type: LogicalFieldType,
  qualified: Boolean = true,
): String {

  val output = getFullyQualifiedKotlinTypeLiteral(type)

  if (qualified) {
    return output
  }

  if (!type.isParameterized) {
    return output.substringAfterLast(".")
  }

  return unqualifyKotlinType(output)
}

//TODO: handle unsigned types
fun getFullyQualifiedKotlinTypeLiteral(type: LogicalFieldType) = when (type.base) {
  ARRAY -> getKotlinArrayType(type.base, type.typeParameters)
  BOOLEAN -> "Boolean"
  CHAR -> "Char"
  DURATION -> "kotlin.time.Duration"
  FLOAT_32 -> "Float"
  FLOAT_64 -> "Double"
  FLOAT_BIG -> "java.math.BigDecimal"
  INT_128 -> "java.math.BigInteger"
  INT_16 -> "Short"
  INT_32 -> "Int"
  INT_64 -> "Long"
  INT_8 -> "Byte"
  INT_BIG -> "java.math.BigInteger"
  LIST -> "List<${type.typeParameters[0]}>"
  MAP -> "Map<${type.typeParameters[0]}, ${type.typeParameters[1]}>"
  MONTH_DAY -> "java.time.MonthDay"
  PATH -> "java.nio.file.Path"
  PERIOD -> "java.time.Period"
  SET -> "Set<${type.typeParameters[0]}>"
  STRING -> "String"
  URI -> "java.net.URI"
  URL -> "java.net.URL"
  UTC_INSTANT -> "java.time.Instant"
  UTC_TIME -> "java.time.OffsetTime"
  UUID -> "java.util.UUID"
  YEAR -> "java.time.Year"
  YEAR_MONTH -> "java.time.YearMonth"
  ZONE_AGNOSTIC_DATE -> "java.time.LocalDate"
  ZONE_AGNOSTIC_TIME -> "java.time.LocalTime"
  ZONE_OFFSET -> "java.time.ZoneOffset"
  ZONED_DATE_TIME -> "java.time.ZonedDateTime"
  USER_DEFINED -> type.rawTypeLiteral //TODO: might need to convert if specified in non-jvm

}.let {
  if (type.nullable) "$it?" else it
}


/**
 * See https://kotlinlang.org/docs/basic-types.html
 *
 * @return kotlin array type literal
 */
private fun getKotlinArrayType(
  base: BaseFieldType,
  typeParameters: List<String>,
): String {
  check(base == ARRAY) { "Only invoke for arrays" }
  check(typeParameters.size == 1) { "exactly 1 type param required" }

  if (
    setOf(
      "byte",
      "int8",
      "java.lang.byte",
      "kotlin.byte",
    ).contains(typeParameters[0].lowercase())
  ) {
    return "ByteArray"
  }

  if (
    setOf(
      "int",
      "int32",
      "integer",
      "java.lang.int",
      "java.lang.integer",
      "kotlin.int",
    ).contains(typeParameters[0].lowercase())
  ) {
    return "IntArray"
  }

  if (
    setOf(
      "double",
      "float64",
      "java.lang.double",
      "kotlin.double",
    ).contains(typeParameters[0].lowercase())
  ) {
    return "DoubleArray"
  }

  TODO("determine kotlin array type: base=$base, typeParameters=$typeParameters")
}


/**
 * @return true when JVM compiler cannot automatically resolve the type
 */
fun kotlinTypeRequiresImport(fullyQualifiedJavaType: String): Boolean {
  if (fullyQualifiedJavaType.startsWith("java.lang.")) {
    return false
  }

  //TODO: is this robust enough?
  if (fullyQualifiedJavaType.startsWith("kotlin.")) {
    return false
  }


  // primitives
  if (!fullyQualifiedJavaType.contains(".")) {
    return false
  }

  return true
}

/**
 * @return comma separated method args clause
 */
fun kotlinMethodArgsForFields(
  fields: Collection<Field>,
  qualified: Boolean,
) =
  fields.joinToString(", ") {
    "${it.name.lowerCamel}: ${getKotlinTypeLiteral(it.type, qualified)}"
  }


//TODO: the return on investment is low here
private fun unqualifyKotlinType(fullyQualifiedKotlinType: String): String {

  //TODO: handle arrays

//  Set<com.wcarmon.chrono.model.ChronoTag>

  // eg. "java.util.Set" or "java.util.List"
  val delim = "<"
  val qualifiedUnparameterizedType = fullyQualifiedKotlinType.substringBefore(delim)

  return qualifiedUnparameterizedType.substringAfterLast(".") +
      delim +
      fullyQualifiedKotlinType.substringAfter(delim)
}
