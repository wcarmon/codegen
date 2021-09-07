package com.wcarmon.codegen.database

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.MariaDBContainerProvider
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
internal class MariaDBTest {

  companion object {

    /** Uses ClassLoader::getResource */
    private const val INIT_SCRIPT_URI = "db-init-scripts/init.mariadb.sql"
    private const val DB_IMAGE_VERSION = "10.5.12"

    private lateinit var container: JdbcDatabaseContainer<*>
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeAll
    @JvmStatic
    fun beforeClass() {

      container = MariaDBContainerProvider()
        .newInstance(DB_IMAGE_VERSION)
        .withInitScript(INIT_SCRIPT_URI)
      container.start()

      jdbcTemplate = buildJDBCTemplate(container)
    }

    @AfterAll
    fun afterClass() = container.stop()
  }

  @BeforeEach
  fun setUp() {
    //
  }

  @Test
  fun foo() {

    val sql = """
      |SELECT id, name 
      |FROM `languages`"""
      .trimMargin()

    jdbcTemplate
      .queryForList(sql)
      .forEach { row ->
        println(row["id"])
        println(row["name"])
      }
  }
}
