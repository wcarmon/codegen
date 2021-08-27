@file:JvmName("TypescriptFieldUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.LogicalFieldType

//TODO: handle enums
fun LogicalFieldType.asTS(): String = when (base) {
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
