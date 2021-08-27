package com.wcarmon.codegen.model

/**
 * See [java.sql.PreparedStatement]
 */
data class JDBCColumnIndex(
  val value: Int,
) {

  companion object {
    val FIRST: JDBCColumnIndex = JDBCColumnIndex(1)
  }

  init {
    require(value >= 1) {
      "JDBC column indexes start at 1: columnIndex=$value"
    }
  }
}
