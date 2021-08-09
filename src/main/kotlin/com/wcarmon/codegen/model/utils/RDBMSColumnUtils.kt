@file:JvmName("RDBMSColumnUtils")

package com.wcarmon.codegen.model.utils

import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field

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
