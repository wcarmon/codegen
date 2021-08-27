package com.wcarmon.codegen.model

/**
 * [Entity] attributes specific to relational database Table
 *
 * See src/main/resources/json-schema/rdbms-table.schema.json
 */
data class RDBMSTableConfig(
  val schema: String = "",
  // TODO: unique index & order
) {

  init {
    require(schema == schema.trim()) {
      "schema must be trimmed: $schema"
    }
  }
}
