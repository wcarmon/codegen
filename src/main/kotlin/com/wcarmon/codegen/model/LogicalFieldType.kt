package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.wcarmon.codegen.model.BaseFieldType.*

class LogicalFieldType(
  val base: BaseFieldType,
  val nullable: Boolean = false,

  // -- Only numeric types
  val precision: Int = 0, // total # significant digits (both sides of decimal point)
  val scale: Int = 0,     // # decimal digits
  val signed: Boolean = true,

  // -- Only on collections
  val typeParameters: List<String> = listOf(),
) {

  companion object {

    /**
     * See [BaseFieldType] for supported literals
     * See [LogicalFieldTypeTest] for examples
     */
    @JvmStatic
    @JsonCreator
    fun parse(literal: String): LogicalFieldType = TODO()
  }

  init {
    require(precision <= 1_000) { "precision too high: $precision" }
    require(precision >= 0) { "precision too low: $precision" }

    require(scale <= precision) { "Scale too high: scale=$scale, precision=$precision" }
    require(scale >= 0) { "Scale too low: $scale" }

    if (base.requiresPrecision()) {
      require(precision > 0) { "Precision too low: $precision" }
    } else {
      require(precision == 0) { "Only numeric types can have precision" }
    }

    if (!base.canHaveScale()) {
      //TODO: missing context
      require(scale == 0) { "field cannot have scale" }
    }

    val n = base.requiredTypeParameterCount()
    when (n) {
      //TODO: missing context
      0 -> require(typeParameters.isEmpty()) {
        "type parameter not allowed"
      }

      1 -> require(typeParameters.size == n) {
        "exactly 1 type parameter required"
      }

      else -> require(typeParameters.size == n) {
        "type parameters required: requiredCount=$n, actualCount=${typeParameters.size}"
      }
    }
  }


  fun asC(): String {
    TODO()
  }

  fun asDart(): String {
    TODO()
  }

  fun asGolang(): String {
    TODO()
  }

  //TODO: handle enums
  fun asJava(): String = when (base) {
    ARRAY -> typeParameters.first() + "[]"
    BOOLEAN -> if (nullable) "Boolean" else "boolean"
    CHAR -> if (nullable) "Character" else "char"
    DURATION -> "java.time.Duration"
    FLOAT_32 -> if (nullable) "Float" else "float"
    FLOAT_64 -> if (nullable) "Double" else "double"
    FLOAT_BIG -> "java.math.BigDecimal"
    INT_128 -> "java.math.BigInteger"
    INT_16 -> if (nullable) "Short" else "short"
    INT_32 -> if (nullable) "Integer" else "int"
    INT_64 -> if (nullable) "Long" else "long"
    INT_8 -> if (nullable) "Byte" else "byte"
    INT_BIG -> "java.math.BigInteger"
    LIST -> "java.util.List<${typeParameters[0]}>"
    MAP -> "java.util.Map<${typeParameters[0]}, ${typeParameters[1]}>"
    MONTH_DAY -> "java.time.MonthDay"
    PATH -> "java.nio.file.Path"
    PERIOD -> "java.time.Period"
    SET -> "java.util.Set<${typeParameters[0]}>"
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
  }

  //TODO: handle enums
  fun asKotlin(): String = when (base) {
    ARRAY -> getKotlinArrayType()
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
    LIST -> "List<${typeParameters[0]}>"
    MAP -> "Map<${typeParameters[0]}, ${typeParameters[1]}>"
    MONTH_DAY -> "java.time.MonthDay"
    PATH -> "java.nio.file.Path"
    PERIOD -> "java.time.Period"
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
    ZONE_AGNOSTIC_TIME -> "java.time.LocalTime"
    ZONE_OFFSET -> "java.time.ZoneOffset"
    ZONED_DATE_TIME -> "java.time.ZonedDateTime"
  }.let {
    if (nullable) "$it?" else it
  }

  fun asRust(): String {
    TODO()
  }

  //TODO: can you align Postgres/MySQL/Oracle
  fun asSQL(): String {
    TODO()
  }

  fun asSwift(): String {
    TODO()
  }

  //TODO: handle enums
  fun asTS(): String = when (base) {
    BOOLEAN -> "boolean"
    UTC_INSTANT -> "Date"

    CHAR,
    DURATION,
    MONTH_DAY,
    PATH,
    PERIOD,
    STRING,
    URI,
    URL,
    UTC_TIME,
    UUID,
    YEAR_MONTH,
    ZONE_AGNOSTIC_DATE,
    ZONE_AGNOSTIC_TIME,
    ZONED_DATE_TIME,
    -> "string"

    FLOAT_32,
    FLOAT_64,
    FLOAT_BIG,
    INT_128,
    INT_16,
    INT_32,
    INT_64,
    INT_8,
    INT_BIG,
    ZONE_OFFSET,
    -> "number"

    ARRAY,
    LIST,
    -> TODO("handle ts arrays")

    SET -> TODO("Set or array?")
    MAP -> TODO("object or Map?")
    YEAR -> TODO("handle year")
  }

  fun asVala(): String {
    TODO()
  }

  private fun getKotlinArrayType(): String {
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

    TODO("determine kotlin array type: $this")
  }
}
