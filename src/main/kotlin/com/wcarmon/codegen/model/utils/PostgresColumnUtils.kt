@file:JvmName("PostgresColumnUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.LogicalFieldType

//TODO: handle enums
fun asPostgreSQL(type: LogicalFieldType, varcharLength: Int = 0): String {
  require(varcharLength >= 0) { "varcharLength too low: $varcharLength" }

  return when (type.base) {
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

    USER_DEFINED -> TODO("convert $type")
  }
}
