@file:JvmName("RDBMSColumnUtils")

package com.wcarmon.codegen.model.utils

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.QuoteType

fun commaSeparatedColumns(entity: Entity): String {

  val pk = entity.primaryKeyFields
    .map { it.name.lowerSnake }

  val nonPK = entity.fields
    .filter { it.rdbms == null || it.rdbms.positionInPrimaryKey == null }
    .map { it.name.lowerSnake }
    .sorted()

  return (pk + nonPK).joinToString()
}

/**
 * Compatible with jdbcTemplate & [java.sql.PreparedStatements]
 *
 * Useful for INSERT & UPDATE statements
 *
 * foo=?, bar=?
 */
fun commaSeparatedColumnAssignment(fields: List<Field>) = fields
  .map { "${it.name.lowerSnake}=?" }
  .joinToString()


/**
 * TODO: ...
 */
fun primaryKeyTableConstraint(entity: Entity): String {
  if (!entity.hasPrimaryKeyFields) {
    return ""
  }

  val csv = entity.primaryKeyFields
    .map { "\"${it.name.lowerSnake}\"" }
    .joinToString(",")

  return "PRIMARY KEY ($csv)"
}


/**
 * SQLite: https://www.sqlite.org/lang_keywords.html
 * PostgreSQL: https://www.postgresql.org/docs/current/sql-syntax-lexical.html
 * Oracle: TODO
 * DB2: TODO
 */
fun quoteTypeForRDBMSLiteral(base: BaseFieldType): QuoteType = when (base) {

  CHAR -> QuoteType.SINGLE

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
  -> QuoteType.NONE

  FLOAT_BIG,
  INT_BIG,
  -> TODO("Determine quote type for JVM literal: $base")

  else -> QuoteType.SINGLE
}

/**
 * TODO: ...
 */
//TODO: more tests here
fun rdbmsDefaultValueLiteral(field: Field): String {
  if (field.defaultValue == null) {
    return ""
  }

  if (field.shouldDefaultToNull) {
    return "NULL"
  }

  return quoteTypeForRDBMSLiteral(field.type.base)
    .wrap(field.defaultValue)
}
