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
      "columnIndex starts at 1: columnIndex=$value"
    }
  }
}
