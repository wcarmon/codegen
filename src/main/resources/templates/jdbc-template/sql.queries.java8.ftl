package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

/**
 * SQL Queries
 * <p>
 * Compatible with PostgreSQL, MariaDB, MySQL, Oracle, SQLite, DB2
 * <p>
 * Useful for {@link java.sql.PreparedStatement} and {@link org.springframework.jdbc.core.JdbcTemplate}
 */
public final class SQLQueries {
<#list entities as entity>

  <#if entity.hasIdFields>
  /**
   * Find-by-PK
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   * Columns count: ${entity.fields?size}
   */
  public static final String SELECT_BY_PK__${entity.name.upperSnake} =
    "SELECT ${entity.rdbmsView.commaSeparatedColumns}"
    + " FROM ${entity.rdbmsView.qualifiedTableName}"
    + " WHERE ${entity.rdbmsView.primaryKeyWhereClause_questionMarks}";

  /**
   * Test for existence of 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   */
  public static final String ROW_EXISTS__${entity.name.upperSnake} =
    "SELECT COUNT(*)"
    + " FROM ${entity.rdbmsView.qualifiedTableName}"
    + " WHERE ${entity.rdbmsView.primaryKeyWhereClause_questionMarks}";

  /**
   * Update 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   * Columns count: ${entity.fields?size}
   */
  public static final String UPDATE__${entity.name.upperSnake} =
    "UPDATE ${entity.rdbmsView.qualifiedTableName}"
    + " SET"
    + " ${entity.rdbmsView.updateSetClause_questionMarks}"
    + " WHERE ${entity.rdbmsView.primaryKeyWhereClause_questionMarks}";

  /**
   * Delete 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   */
  public static final String DELETE__${entity.name.upperSnake} =
    "DELETE FROM ${entity.rdbmsView.qualifiedTableName}"
    + " WHERE ${entity.rdbmsView.primaryKeyWhereClause_questionMarks}";
  </#if>

  /**
   * Select all rows
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * Columns count: ${entity.fields?size}
   */
  public static final String SELECT_ALL__${entity.name.upperSnake} =
    "SELECT ${entity.rdbmsView.commaSeparatedColumns}"
    + " FROM ${entity.rdbmsView.qualifiedTableName}";

  /**
   * Insert 1-row
   * Entity: {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * PK column count: ${entity.idFields?size}
   * Columns count: ${entity.fields?size}
   */
  public static final String INSERT__${entity.name.upperSnake} =
    "INSERT INTO ${entity.rdbmsView.qualifiedTableName}"
    + " (${entity.rdbmsView.commaSeparatedColumns})"
    + " VALUES (${entity.rdbmsView.questionMarkStringForInsert})";

${entity.java8View.patchQueries()}

</#list>
}
