@file:JvmName("SQLQueries")
package ${request.packageName.value}

/* -------------------------------------------------------------------
 * SQL Queries
 *
 * Compatible with PostgreSQL, MariaDB, MySQL, Oracle, SQLite, DB2
 *
 * Useful for {@link java.sql.PreparedStatement}
 *   and {@link org.springframework.jdbc.core.JdbcTemplate}
 *
 * See: ${request.prettyTemplateName}
 * -------------------------------------------------------------------
 */
<#list entities as entity>

  <#if entity.hasPrimaryKeyFields>
  /**
   * Find-by-PK
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.primaryKeyFields?size}
   * Columns count: ${entity.fields?size}
   */
  val SELECT_BY_PK__${entity.name.upperSnake} =
    """
    SELECT $entity.commaSeparatedColumns
    FROM "${entity.name.lowerSnake}"
    WHERE $entity.pkWhereClause"
    """.trimIndent()

  /**
   * Test for existence of 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.primaryKeyFields?size}
   */
  val ROW_EXISTS__${entity.name.upperSnake} =
    """
    SELECT COUNT(*)
    FROM "${entity.name.lowerSnake}"
    WHERE $entity.pkWhereClause
    """.trimIndent()

  /**
   * Update 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.primaryKeyFields?size}
   * Columns count: ${entity.fields?size}
   */
  val UPDATE__${entity.name.upperSnake} =
    """
    UPDATE "${entity.name.lowerSnake}"
    SET
      $entity.updateSetClause
    WHERE $entity.pkWhereClause
    """.trimIndent()

  /**
   * Delete 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.primaryKeyFields?size}
   */
  val DELETE__${entity.name.upperSnake} =
    """
    DELETE FROM "${entity.name.lowerSnake}"
    WHERE $entity.pkWhereClause
    """.trimIndent()
  </#if>

  /**
   * Select all rows
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * Columns count: ${entity.fields?size}
   */
  val SELECT_ALL__${entity.name.upperSnake} =
    """
    SELECT $entity.commaSeparatedColumns
    FROM "${entity.name.lowerSnake}"
    """.trimIndent()

  /**
   * Insert 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.primaryKeyFields?size}
   * Columns count: ${entity.fields?size}
   */
  val INSERT__${entity.name.upperSnake} =
    """
    INSERT INTO "${entity.name.lowerSnake}" (
      $entity.commaSeparatedColumns
    )
    VALUES ($entity.questionMarkStringForInsert)
    """.trimIndent()

  <#if entity.hasPrimaryKeyFields>
    <#list entity.nonPrimaryKeyFields as field>
    val PATCH__${entity.name.upperSnake}__${field.name.upperSnake} =
      """
      UPDATE "${entity.name.lowerSnake}"
      SET $field.name.lowerSnake=?
      WHERE $entity.pkWhereClause
      """.trimIndent()

    </#list>
  </#if>

</#list>
