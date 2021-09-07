package com.wcarmon.codegen.database

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.testcontainers.containers.JdbcDatabaseContainer


fun buildDataSource(container: JdbcDatabaseContainer<*>): DriverManagerDataSource =
  DriverManagerDataSource(
    container.jdbcUrl,
    container.username,
    container.password
  )

fun buildJDBCTemplate(container: JdbcDatabaseContainer<*>): JdbcTemplate =
  JdbcTemplate(buildDataSource(container))
