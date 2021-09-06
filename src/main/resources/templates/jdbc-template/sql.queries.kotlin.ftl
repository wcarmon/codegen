@file:JvmName("SQLQueries")
package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

/* -------------------------------------------------------------------
 * SQL Queries
 *
 * Compatible with PostgreSQL, MariaDB, MySQL, Oracle, SQLite, DB2
 *
 * Useful for {@link java.sql.PreparedStatement}
 *   and {@link org.springframework.jdbc.core.JdbcTemplate}
 * -------------------------------------------------------------------
 */
<#list entities as entity>

  <#if entity.hasIdFields>
  /**
   * Find-by-PK
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   * Columns count: ${entity.fields?size}
   */
  val SELECT_BY_PK__${entity.name.upperSnake} =
    """
    |SELECT ${entity.rdbmsView.commaSeparatedColumns}
    |FROM "${entity.name.lowerSnake}"
    |WHERE ${entity.rdbmsView.primaryKeyWhereClause}"
    """.trimMargin()

  /**
   * Test for existence of 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   */
  val ROW_EXISTS__${entity.name.upperSnake} =
    """
    |SELECT COUNT(*)
    |FROM "${entity.name.lowerSnake}"
    |WHERE ${entity.rdbmsView.primaryKeyWhereClause}
    """.trimMargin()

  /**
   * Update 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   * Columns count: ${entity.fields?size}
   */
  val UPDATE__${entity.name.upperSnake} =
    """
    |UPDATE "${entity.name.lowerSnake}"
    |SET
    |  ${entity.rdbmsView.updateSetClause}
    |WHERE ${entity.rdbmsView.primaryKeyWhereClause}
    """.trimMargin()

  /**
   * Delete 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   */
  val DELETE__${entity.name.upperSnake} =
    """
    |DELETE FROM "${entity.name.lowerSnake}"
    |WHERE ${entity.rdbmsView.primaryKeyWhereClause}
    """.trimMargin()
  </#if>

  /**
   * Select all rows
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * Columns count: ${entity.fields?size}
   */
  val SELECT_ALL__${entity.name.upperSnake} =
    """
    |SELECT ${entity.rdbmsView.commaSeparatedColumns}
    |FROM "${entity.name.lowerSnake}"
    """.trimMargin()

  /**
   * Insert 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   * Columns count: ${entity.fields?size}
   */
  val INSERT__${entity.name.upperSnake} =
    """
    |INSERT INTO "${entity.name.lowerSnake}" (
    |  ${entity.rdbmsView.commaSeparatedColumns}
    |)
    |VALUES (${entity.rdbmsView.questionMarkStringForInsert})
    """.trimMargin()

    ${entity.kotlinView.patchQueries()}

</#list>
