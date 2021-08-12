@file:JvmName("RDBMSColumnUtils")

/** Utilities common to all RDBMS */
package com.wcarmon.codegen.model.utils

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.QuoteType
import com.wcarmon.codegen.model.QuoteType.*

/**
 * @return comma separated column names, PK fields first
 */
//TODO: decide when to quote
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
 * Compatible with [org.springframework.jdbc.core.JdbcTemplate]
 * Compatible with [java.sql.PreparedStatements]
 *
 * Useful for INSERT & UPDATE statements
 *
 * @return comma separated column assignment expressions (eg. "foo=?, bar=?")
 */
fun commaSeparatedColumnAssignment(fields: List<Field>) = fields
  .map { "${it.name.lowerSnake}=?" }
  .joinToString()


/**
 * DB2:     https://www.ibm.com/docs/en/db2/10.1.0?topic=rules-create-table
 * H2:      http://h2database.com/html/commands.html#create_table
 * Maria:   https://mariadb.com/kb/en/create-table/
 * MySQL:   TODO:
 * Oracle:  https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_7002.htm
 * PG:      https://www.postgresql.org/docs/current/sql-createtable.html
 * SQLite:  https://www.sqlite.org/syntax/table-constraint.html
 *
 * @return Primary key (Table) Constraint expression
 * (not a column constraint)
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

/**
 * Db2:     TODO
 * H2:      TODO
 * Maria:   TODO
 * MySQL:   TODO
 * Oracle:  TODO
 * PG:      https://www.postgresql.org/docs/current/sql-createtable.html
 * SQLite:  TODO
 *
 * Adds appropriate quotes when required
 *
 * @return literal for Default value (called "default_expr" in PostgreSQL AST)
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
