package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.util.postgresColumnDefinition
import com.wcarmon.codegen.model.util.sqliteColumnDefinition

/**
 * RDBMS related convenience methods for a [Field]
 * See [com.wcarmon.codegen.model.RDBMSColumnConfig]
 */
data class RDBMSColumnView(
  private val field: Field,
) {

  val isPrimaryKeyField = (field.rdbmsConfig.positionInPrimaryKey ?: -1) >= 0

  /**
   * Works on PostgreSQL, H2, Maria, MySQL, DB2
   * (maybe oracle too, but they make it near impossible to test)
   */
  val postgresqlColumnDefinition = postgresColumnDefinition(field)

  /**
   * Works directly on SQLite (without using their affinity conversion layer)
   */
  val sqliteColumnDefinition = sqliteColumnDefinition(field)
}
