@file:JvmName("RDBMSColumnUtils")

/** Utilities common to all RDBMS */
package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.QuoteType.*
import com.wcarmon.codegen.model.SQLPlaceholderType.*

/**
 * @return comma separated column names, PK fields first
 */
//TODO: decide when to quote
fun commaSeparatedColumns(entity: Entity): String =
  (entity.idFields + entity.nonIdFields)
    .joinToString(
      separator = ", "
    ) {
      it.name.lowerSnake
    }

/**
 * Compatible with [org.springframework.jdbc.core.JdbcTemplate]
 * Compatible with [java.sql.PreparedStatements]
 *
 * Useful for INSERT & UPDATE statements
 *
 * @return comma separated column assignment expressions (eg. "foo=?, bar=?")
 */
fun commaSeparatedColumnAssignment(
  fields: List<Field>,
  placeholderType: SQLPlaceholderType,

  // Only for numbered
  firstIndex: Int = 1,
): String = when (placeholderType) {

  QUESTION_MARK ->
    fields.joinToString { "${it.name.lowerSnake}=?" }

  NUMBERED_DOLLARS -> {
    require(firstIndex >= 1) {
      "dollar indexes start at 1: firstIndex=$firstIndex"
    }

    fields
      .mapIndexed { index, field ->
        "${field.name.lowerSnake}=\u0024${index + firstIndex}"
      }
      .joinToString(", ")
  }

  NAMED_PARAMS -> TODO()
}


/**
 * DB2:     https://www.ibm.com/docs/en/db2/10.1.0?topic=rules-create-table
 * H2:      http://h2database.com/html/commands.html#create_table
 * Maria:   https://mariadb.com/kb/en/create-table/
 * MySQL:   TODO:
 * Oracle:  https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_7002.htm
 * Postgre: https://www.postgresql.org/docs/current/sql-createtable.html
 * SQLite:  https://www.sqlite.org/syntax/table-constraint.html
 *
 * @return Primary key (Table) Constraint expression
 * (not a column constraint)
 */
fun primaryKeyTableConstraint(entity: Entity): String {
  if (!entity.hasIdFields) {
    return ""
  }

  val csv = entity.idFields.joinToString(",") { "\"${it.name.lowerSnake}\"" }

  return "PRIMARY KEY ($csv)"
}


/**
 * DB2:         https://www.ibm.com/support/producthub/db2/docs/content/SSEPGG_11.5.0/com.ibm.db2.luw.xml.doc/doc/xqrliteral.html
 * H2:          http://www.h2database.com/html/grammar.html#string
 * Maria:       https://mariadb.com/kb/en/string-literals/
 * MySQL:       TODO:
 * Oracle:      https://docs.oracle.com/cd/B19306_01/server.102/b14200/sql_elements003.htm
 * PostgreSQL:  https://www.postgresql.org/docs/current/sql-syntax-lexical.html
 * SQLite:      https://www.sqlite.org/lang_keywords.html
 *
 * @return Quote type for the logical base type
 */
fun quoteTypeForRDBMSLiteral(base: BaseFieldType): QuoteType = when (base) {

  CHAR -> SINGLE

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

  else -> SINGLE
}

