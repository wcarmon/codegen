package com.wcarmon.codegen.model

/**
 * Represents a type in many popular languages.
 *
 * Modifiers like nullable, generics, mutable are in [LogicalFieldType]
 */
enum class BaseFieldType {

  BOOLEAN,

  CHAR, // 16-bit Unicode character
  PATH,
  STRING,
  URI,
  URL,
  UUID,

  FLOAT_32,
  FLOAT_64,
  FLOAT_BIG,
  INT_128,
  INT_16,
  INT_32,
  INT_64,
  INT_8,
  INT_BIG,

  DURATION,
  INSTANT,
  PERIOD,

  ARRAY,
  LIST,
  MAP,
  SET,
  ;

  companion object {

    @JvmStatic
    fun parse(value: String): BaseFieldType =
      MAPPINGS.getOrDefault(value, null)
        ?: throw IllegalArgumentException("Failed to parse base type for value=$value")

    private val MAPPINGS = mapOf(
      "golang.bool" to BOOLEAN,
      "golang.byte" to INT_8,
      "golang.float32" to FLOAT_32,
      "golang.float64" to FLOAT_64,
      "golang.int16" to INT_16,
      "golang.int32" to INT_32,
      "golang.int64" to INT_64,
      "golang.int8" to INT_8,
      "golang.rune" to INT_32,
      "golang.string" to STRING,
      "golang.uint16" to INT_16,
      "golang.uint32" to INT_32,
      "golang.uint64" to INT_64,
      "golang.uint8" to INT_16,
      "java.io.File" to PATH,
      "java.lang.Boolean" to BOOLEAN,
      "java.net.URI" to URI,
      "java.net.URL" to URL,
      "java.nio.file.Path" to PATH,
      "java.util.List" to LIST,
      "java.util.Map" to MAP,
      "java.util.Set" to SET,
      "java.util.UUID" to UUID,

      //TODO: golang.complex128
      //TODO: golang.complex64
      //TODO: golang.int
      //TODO: golang.math.big.Float
      //TODO: golang.math.big.Int
      //TODO: golang.time.Duration
      //TODO: golang.time.Location
      //TODO: golang.time.Time
      //TODO: golang.uint
      //TODO: golang.uintptr

      //TODO: java.math.BigDecimal
      //TODO: java.math.BigInteger
      //TODO: java.sql.*
      //TODO: java.sql.Date
      //TODO: java.sql.Timestamp
      //TODO: java.time.Duration
      //TODO: java.time.Instant
      //TODO: java.time.LocalDate
      //TODO: java.time.LocalDateTime
      //TODO: java.time.LocalTime
      //TODO: java.time.MonthDay
      //TODO: java.time.OffsetDateTime
      //TODO: java.time.OffsetTime
      //TODO: java.time.Period
      //TODO: java.time.Year
      //TODO: java.time.YearMonth
      //TODO: java.time.ZonedDateTime
      //TODO: java.time.ZonedDateTime
      //TODO: java.time.ZoneId
      //TODO: java.time.ZoneOffset
      //TODO: java.util.Date

      //TODO: kotlin.CharProgression
      //TODO: kotlin.CharProgression
      //TODO: kotlin.CharRange
      //TODO: kotlin.collections.List
      //TODO: kotlin.collections.Map
      //TODO: kotlin.collections.Set
      //TODO: kotlin.IntProgression
      //TODO: kotlin.IntRange
      //TODO: kotlin.LongProgression
      //TODO: kotlin.LongRange
      //TODO: kotlin.UIntProgression
      //TODO: kotlin.UIntRange
      //TODO: kotlin.ULongProgression
      //TODO: kotlin.ULongRange
      //TODO: kotlin.Int
      //TODO: kotlin.Byte
      //TODO: kotlin.Short
      //TODO: kotlin.Long
      //TODO: kotlin.Float
      //TODO: kotlin.Double

      //TODO: rust
      //TODO: sql
    )
  }

  /** true for String, Collections, Enums, Arrays */
  fun isParameterized(): Boolean {
    TODO()
  }
}
