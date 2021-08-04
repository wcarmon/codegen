package com.wcarmon.codegen.model

import com.wcarmon.codegen.model.BaseFieldType.*

/**
 * Represents all the aspects of a field's type (in popular languages)
 *
 * [BaseFieldType] handles most of the logic for predefined types
 * Generics are handled by [typeParameters] (parametric polymorphism)
 */
data class LogicalFieldType(
  val base: BaseFieldType,
  val nullable: Boolean = false,

  // -- Only numeric types
  val precision: Int = 0, // total # significant digits (both sides of decimal point)
  val scale: Int = 0,     // # decimal digits
  val signed: Boolean = true,

  /** Useful for user-defined types, fully qualified */
  val rawTypeLiteral: String,

  /** Is this type limited to a bounded set of values? */
  val enumType: Boolean = false,

  // -- Only for Collections & Generic types (Parametric polymorphism)
  val typeParameters: List<String> = listOf(),
) {

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
        "exactly 1-type parameter required (add 'typeParameters' to Field)"
      }

      else -> require(typeParameters.size == n) {
        "type parameters required (add 'typeParameters' to Field): requiredCount=$n, actualCount=${typeParameters.size}"
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
    USER_DEFINED -> rawTypeLiteral //TODO: might need to convert if specified in non-jvm
  }

  //TODO: handle unsigned types
  fun asKotlin() = when (base) {
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
    USER_DEFINED -> rawTypeLiteral //TODO: might need to convert if specified in non-jvm

  }.let {
    if (nullable) "$it?" else it
  }

  fun asRust(): String {
    TODO()
  }

  //TODO: handle enums
  fun asPostgreSQL(varcharLength: Int = 0): String {
    require(varcharLength >= 0) { "varcharLength too low: $varcharLength" }

    return when (base) {
      ARRAY -> TODO()
      BOOLEAN -> "BOOLEAN"
      CHAR -> TODO()
      DURATION -> "INTERVAL"
      FLOAT_32 -> "FLOAT4"
      FLOAT_64 -> "FLOAT8"
      FLOAT_BIG -> TODO()
      INT_128 -> TODO()
      INT_16 -> "INT2"
      INT_32 -> "INT4"
      INT_64 -> "INT8"
      INT_8 -> "SMALLINT"
      INT_BIG -> TODO()
      LIST -> TODO()  // comma separated?
      MAP -> TODO()
      MONTH_DAY -> "VARCHAR(16)"
      PERIOD -> "INTERVAL"
      SET -> TODO() // comma separated?
      UTC_INSTANT -> "TIMESTAMP WITHOUT TIME ZONE"
      UTC_TIME -> TODO()
      UUID -> "UUID"
      YEAR -> "INT4"
      YEAR_MONTH -> "VARCHAR(32)"
      ZONE_AGNOSTIC_DATE -> "DATE"
      ZONE_AGNOSTIC_TIME -> TODO()
      ZONE_OFFSET -> "INT4"
      ZONED_DATE_TIME -> TODO()

      //TODO: allow param to override
      PATH -> "VARCHAR(256)"
      URL -> "VARCHAR(2048)"
      URI,
      STRING,
      -> "VARCHAR($varcharLength)"

      USER_DEFINED -> TODO("convert $rawTypeLiteral")
    }
  }

  fun asSQLite(): String {
    TODO()
  }

  fun asMySQL(): String {
    TODO()
  }

  fun asOracle(): String {
    TODO()
  }

  fun asSQLiteDelight(): String {
    TODO()
  }

  fun asProtoBuf(): String {
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

    USER_DEFINED -> TODO("convert $rawTypeLiteral")
  }

  fun asSwagger(): String {
    TODO()
  }

  fun asVala(): String {
    TODO()
  }

  /**
   * Template should prefix "new" when required
   */
  fun newJavaCollectionExpression(): String {

    require(base.isCollection()) {
      "method only for collections: $this"
    }

    return when (base) {
      ARRAY -> TODO("Handle creating arrays (need to know size)")
      LIST -> "ArrayList<>()"
      MAP -> "HashMap<>()"
      SET -> "HashSet<>()"
      else -> TODO("Handle instantiating: $this")
    }
  }

  fun unmodifiableJavaCollectionMethod(): String {
    require(base.isCollection()) {
      "method only for collections: $this"
    }

    return when (base) {
      LIST -> "Collections.unmodifiableList"
      MAP -> "Collections.unmodifiableMap"
      SET -> "Collections.unmodifiableSet"
      else -> TODO("Handle immutable version of: $this")
    }
  }

  fun jdbcGetter(): String = when (base) {
    BOOLEAN -> "getBoolean"
    FLOAT_32 -> "getFloat"
    FLOAT_64 -> "getDouble"
    FLOAT_BIG -> "getBigDecimal"
    INT_16 -> "getShort"
    INT_32 -> "getInt"
    INT_64 -> "getLong"
    UTC_INSTANT -> "getString"
    YEAR -> "getInt"

//    CHAR, // 16-bit Unicode character
//    INT_128
//    INT_8
//    INT_BIG

//    DURATION,           // measured in seconds & nanos
//    MONTH_DAY,          // eg. birthdays
//    PERIOD,             // measured in years, months (day agnostic) or days (time agnostic)
//    UTC_TIME,           // eg. daily meeting time, market opening time (y/m/d agnostic)
//    YEAR_MONTH,         // eg. credit card expiration
//    ZONE_AGNOSTIC_DATE, // eg. birthdate (tz agnostic)
//    ZONE_AGNOSTIC_TIME, // eg. store closing time (tz agnostic)
//    ZONE_OFFSET,        // seconds (with upper bound)
//    ZONED_DATE_TIME,    // Instant + offset + tz rules

    ARRAY,
    LIST,
    MAP,
    PATH,
    SET,
    STRING,
    URI,
    URL,
    UUID,
    -> "getString"


    //TODO: handle when enum puts number in database
    USER_DEFINED ->
      if (enumType) "getString"
      else "getString"  // User has to serialize somehow

    else -> TODO("add jdbc getter for $this")
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

  fun javaStringFactory(): String = when (base) {
    BOOLEAN -> TODO("fix string factory for $this")
    CHAR -> TODO("fix string factory for $this")
    FLOAT_32 -> TODO("fix string factory for $this")
    FLOAT_64 -> TODO("fix string factory for $this")
    FLOAT_BIG -> TODO("fix string factory for $this")
    INT_128 -> TODO("fix string factory for $this")
    INT_8 -> TODO("fix string factory for $this")
    INT_BIG -> TODO("fix string factory for $this")

    ZONE_AGNOSTIC_DATE -> TODO("fix string factory for $this")
    ZONE_AGNOSTIC_TIME -> TODO("fix string factory for $this")
    ZONE_OFFSET -> TODO("fix string factory for $this")
    ZONED_DATE_TIME -> TODO("fix string factory for $this")
    ARRAY -> TODO("fix string factory for $this")
    LIST -> TODO("fix string factory for $this")
    MAP -> TODO("fix string factory for $this")
    SET -> TODO("fix string factory for $this")
    USER_DEFINED -> TODO("fix string factory for $this")

    INT_16 -> "Short.parseShort"
    INT_32 -> "Integer.parseInt"
    INT_64 -> "Long.parseLong"
    PATH -> "${asJava()}.of"
    STRING -> "String.valueOf"  //TODO: seems unnecessary
    URI -> "${asJava()}.create"
    UUID -> "${asJava()}.fromString"

    URL -> "new ${asJava()}"

    DURATION,
    MONTH_DAY,
    PERIOD,
    UTC_INSTANT,
    UTC_TIME,
    YEAR_MONTH,
    YEAR,
    -> "${asJava()}.parse"
  }
}
