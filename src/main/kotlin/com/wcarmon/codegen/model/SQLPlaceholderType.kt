package com.wcarmon.codegen.model

/**
 * See https://github.com/golang/go/wiki/SQLDrivers
 * See [org.springframework.jdbc.core.JdbcTemplate]
 * See [java.sql.PreparedStatement]
 */
enum class SQLPlaceholderType {

  /**
   * -- JVM --
   * [org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate] for all databases
   *
   * -- Golang --
   * Oracle: TODO
   */
  NAMED_PARAMS,

  /**
   * eg. $1, $2, $3, $4
   *
   * -- JVM --
   * NOT USED
   *
   * -- Golang --
   * PostgreSQL: https://pkg.go.dev/github.com/lib/pq
   */
  NUMBERED_DOLLARS,

  /**
   * Most common
   *
   * eg. ?, ?, ?, ?
   *
   * -- JVM --
   * [org.springframework.jdbc.core.JdbcTemplate] for all databases
   * [java.sql.PreparedStatement] for all databases
   *
   * -- Golang --
   * MySQL: github.com/go-sql-driver/mysql
   * MariaDB
   */
  QUESTION_MARK
  ;

  fun firstPlaceholder(): String = forIndex(1)

  fun forIndex(
    index: Int = 1,
  ): String = when (this) {

    NAMED_PARAMS -> throw UnsupportedOperationException("named params don't work by number/index")

    NUMBERED_DOLLARS -> {
      require(index >= 1) { "numbered Dollar placeholders start at 1" }

      "\u0024${index}"
    }

    QUESTION_MARK -> "?"
  }
}

// Golang: Couchbase:
// Golang: Db2: github.com/alexbrainman/odbc <-- question mark
// Golang: Db2: https://github.com/ibmdb/go_ibm_db <--
// Golang: h2:
// Golang: Oracle: https://github.com/godror/godror <-- :1, :2, ...
// Golang: Oracle: https://github.com/sijms/go-ora  <-- :1, :2, ...
// Golang: Oracle: https://pkg.go.dev/gopkg.in/rana/ora.v4#hdr-SQL_Placeholder_Syntax <-- :aa, :bb, :cc
// Golang: SQLite: https://github.com/mattn/go-sqlite3 <--
// Golang: SQLite: https://pkg.go.dev/modernc.org/sqlite <--
