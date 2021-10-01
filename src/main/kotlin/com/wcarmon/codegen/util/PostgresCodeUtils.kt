@file:JvmName("PostgresCodeUtils")

/** Utilities only useful for generating PostgreSQL code */
package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.RDBMSColumnConfig


/**
 * See https://www.postgresql.org/docs/current/datatype.html
 *
 * Derive the correct type
 *
 * @return Literal for PostgreSQL type
 */
fun getPostgresTypeLiteral(
  logicalFieldType: LogicalFieldType,
  rdbmsConfig: RDBMSColumnConfig,
  effectiveBaseType: BaseFieldType = logicalFieldType.base,
  errorLoggingInfo: String,
): String = when (effectiveBaseType) {
  BOOLEAN -> "BOOLEAN"
  CHAR -> "VARCHAR(4)"
  DURATION -> "VARCHAR(40)"         // only need 37
  FLOAT_32 -> "REAL"                // FLOAT4
  FLOAT_64 -> "DOUBLE PRECISION"    // FLOAT8
  INT_128 -> "NUMERIC(20,0)"
  INT_16 -> "SMALLINT"              // INT2 == NUMERIC(3,0)
  INT_64 -> "BIGINT"                // INT8 == NUMERIC(10,0)
  INT_8 -> "SMALLINT"               // INT2 is the smallest, NUMERIC(2,0)
  MONTH_DAY -> "VARCHAR(16)"
  PATH -> "VARCHAR(256)"
  PERIOD -> "VARCHAR(40)"           // only need 37
  URL -> "VARCHAR(2048)"
  UTC_INSTANT -> "VARCHAR(32)"      // only need 27
  UTC_TIME -> "VARCHAR(16)"         // only need 15
  UUID -> "VARCHAR(36)"             // PostgreSQL has a UUID type, but why bother :-)
  YEAR_MONTH -> "VARCHAR(32)"
  ZONE_AGNOSTIC_DATE -> "VARCHAR(16)"
  ZONE_AGNOSTIC_TIME -> "VARCHAR(16)" // only need 12
  ZONED_DATE_TIME -> "VARCHAR(68)"    // some timezone names are long
  INT_32,
  YEAR,
  ZONE_OFFSET,
  -> "INTEGER"                        // INT4 == NUMERIC(5,0)

  INT_BIG -> {
    requireNotNull(logicalFieldType.precision) {
      "Positive field.precision is required: $errorLoggingInfo"
    }

    "NUMERIC(${logicalFieldType.precision}, 0)"
  }

  FLOAT_BIG -> {
    require(logicalFieldType.scale > 0) {
      "Positive field.scale is required for float types: $errorLoggingInfo"
    }

    "NUMERIC(${logicalFieldType.precision}, ${logicalFieldType.scale})"
  }

  COLOR -> "VARCHAR(7)"

  ARRAY,
  EMAIL,
  LIST,
  MAP,
  PHONE_NUMBER,
  SET,
  STRING,
  URI,
  USER_DEFINED,
  -> {
    requireNotNull(rdbmsConfig.varcharLength) {
      "field.rdbms.varcharLength (or field.rdbms.overrideEffectiveType) is required: $errorLoggingInfo"
    }

    "VARCHAR(${rdbmsConfig.varcharLength})"
  }
  WEEK_OF_YEAR -> TODO()
  ZONE_AGNOSTIC_DATE_TIME -> TODO()
}
