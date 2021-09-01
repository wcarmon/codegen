@file:JvmName("SQLDelightUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.QuoteType
import com.wcarmon.codegen.model.QuoteType.DOUBLE
import com.wcarmon.codegen.model.QuoteType.NONE


/**
 * See https://cashapp.github.io/sqldelight/jvm_sqlite/types/
 */
fun sqlDelightTypeLiteral(type: LogicalFieldType): String = when (type.base) {

  BOOLEAN -> "INTEGER AS Boolean"
  CHAR -> TODO()
  FLOAT_32 -> "REAL AS Float"
  FLOAT_64 -> "REAL"
  FLOAT_BIG -> TODO()
  INT_128 -> TODO()
  INT_16 -> "INTEGER AS Short"
  INT_32 -> "INTEGER AS Int"
  INT_64 -> "INTEGER"
  INT_8 -> TODO()
  INT_BIG -> TODO()
  YEAR -> "INTEGER"
  ZONE_OFFSET -> "INTEGER"

  ARRAY,
  DURATION,
  LIST,
  MAP,
  MONTH_DAY,
  PATH,
  PERIOD,
  SET,
  STRING,
  URI,
  URL,
  UTC_INSTANT,
  UTC_TIME,
  UUID,
  YEAR_MONTH,
  ZONE_AGNOSTIC_DATE,
  ZONE_AGNOSTIC_TIME,
  ZONED_DATE_TIME,
  -> "TEXT"

  //TODO: allow override in json config
  USER_DEFINED -> "TEXT"

  else -> TODO("get sqldelight type for: $type")
}


fun quoteTypeForSQLDelightLiteral(base: BaseFieldType): QuoteType = when (base) {

  CHAR -> DOUBLE

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
