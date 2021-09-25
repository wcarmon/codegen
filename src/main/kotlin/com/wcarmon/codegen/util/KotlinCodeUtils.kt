@file:JvmName("KotlinCodeUtils")

/** Utilities only useful for generating Kotlin */
package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage.KOTLIN_JVM_1_4

//TODO: make most of these private

@Suppress("ReturnCount")
fun kotlinTypeLiteral(
  field: Field,
  qualified: Boolean = true,
): String {

  val output = getFullyQualifiedKotlinTypeLiteral(field)

  if (qualified) {
    return output
  }

  if (!field.isParameterized(KOTLIN_JVM_1_4)) {
    return output.substringAfterLast(".")
  }

  return unqualifyKotlinType(output)
}


//TODO: handle unsigned types
private fun getFullyQualifiedKotlinTypeLiteral(
  field: Field,
): String {
  val baseType = field.effectiveBaseType(KOTLIN_JVM_1_4)
  val typeParameters = field.typeParameters(KOTLIN_JVM_1_4)

  return when (baseType) {
    ARRAY -> getKotlinArrayType(baseType, typeParameters)
    BOOLEAN -> "Boolean"
    CHAR -> "Char"
    COLOR -> "String"
    DURATION -> "kotlin.time.Duration"
    EMAIL -> "String"
    FLOAT_32 -> "Float"
    FLOAT_64 -> "Double"
    FLOAT_BIG -> "java.math.BigDecimal"
    INT_128 -> "java.math.BigInteger"
    INT_16 -> "Short"
    INT_32 -> "Int"
    INT_64 -> "Long"
    INT_8 -> "Byte"
    INT_BIG -> "java.math.BigInteger"
    LIST -> "List<${typeParameters[0]}>"
    MAP -> "Map<${typeParameters[0]}, ${typeParameters[1]}>"
    MONTH_DAY -> "java.time.MonthDay"
    PATH -> "java.nio.file.Path"
    PERIOD -> "java.time.Period"
    PHONE_NUMBER -> "String"
    SET -> "Set<${typeParameters[0]}>"
    STRING -> "String"
    URI -> "java.net.URI"
    URL -> "java.net.URL"
    UTC_INSTANT -> "java.time.Instant"
    UTC_TIME -> "java.time.OffsetTime"
    UUID -> "java.util.UUID"
    YEAR -> "java.time.Year"
    YEAR_MONTH -> "java.time.YearMonth"
    ZONE_AGNOSTIC_DATE -> "java.time.LocalDate"
    ZONE_AGNOSTIC_DATE_TIME -> "java.time.LocalDateTime"
    ZONE_AGNOSTIC_TIME -> "java.time.LocalTime"
    ZONE_OFFSET -> "java.time.ZoneOffset"
    ZONED_DATE_TIME -> "java.time.ZonedDateTime"

    //TODO: might need to convert if specified in non-jvm
    USER_DEFINED -> field.type.rawTypeLiteral
    WEEK_OF_YEAR -> TODO()
  }.let {
    if (field.type.nullable) "$it?" else it
  }
}

fun getKotlinImportsForFields(entity: Entity) =
  entity.fields
    .asSequence()
    .filter {
      it.effectiveBaseType(KOTLIN_JVM_1_4) == USER_DEFINED || !it.isParameterized(KOTLIN_JVM_1_4)
    }
    .map { kotlinTypeLiteral(it) }
    .map { it.removeSuffix("?") }
    .filter { kotlinTypeRequiresImport(it) }
    .toSortedSet()


/**
 * @return true when JVM compiler cannot automatically resolve the type
 */
@Suppress("ReturnCount")
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
    "${it.name.lowerCamel}: ${kotlinTypeLiteral(it, qualified)}"
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


/**
 * See https://kotlinlang.org/docs/basic-types.html
 *
 * @return kotlin array type literal
 */
@Suppress("ReturnCount")
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


