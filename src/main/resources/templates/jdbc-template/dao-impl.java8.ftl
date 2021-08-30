package ${request.packageName.value};


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
<#if request.jvmContextClass?has_content>
import ${request.jvmContextClass};
</#if>
<#list entity.java8View.importsForFields as importable>
import ${importable};
</#list>
<#list request.extraJVMImports as importable>
import ${importable};
</#list>
<#if entity.jvmView.requiresObjectWriter>
import com.fasterxml.jackson.databind.ObjectWriter;
</#if>

import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * DAO implementation for {@link ${entity.pkg.value}.${entity.name.upperCamel}}.
 * <p>
 * Uses {@link org.springframework.jdbc.core.JdbcTemplate} to execute queries.
 * <p>
 * Uses {@link java.sql.PreparedStatement}
 * <p>
 * See https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html
 * <p>
 * Threadsafe & Reusable after construction
 */
public final class ${entity.name.upperCamel}DAOImpl implements ${entity.name.upperCamel}DAO {

  private final JdbcTemplate jdbcTemplate;
  <#if entity.jvmView.requiresObjectWriter>
  /** To serialize collection fields */
  private final ObjectWriter objectWriter;
  </#if>
  private final RowMapper<${entity.name.upperCamel}> rowMapper;

  public ${entity.name.upperCamel}DAOImpl(
    JdbcTemplate jdbcTemplate,
    <#if entity.jvmView.requiresObjectWriter>
    ObjectWriter objectWriter,
    </#if>
    RowMapper<${entity.name.upperCamel}> rowMapper) {

    Objects.requireNonNull(jdbcTemplate, "jdbcTemplate is required and null");
    <#if entity.jvmView.requiresObjectWriter>
    Objects.requireNonNull(objectWriter, "objectWriter is required and null");
    </#if>
    Objects.requireNonNull(rowMapper, "rowMapper is required and null");

    this.jdbcTemplate = jdbcTemplate;
    <#if entity.jvmView.requiresObjectWriter>
    this.objectWriter = objectWriter;
    </#if>
    this.rowMapper = rowMapper;
  }

  <#if entity.hasIdFields>
  @Override
  public void delete(${request.unqualifiedContextClass} context,${entity.java8View.methodArgsForIdFields(false)}) {
    Objects.requireNonNull(context, "context is required and null.");
    ${entity.java8View.primaryKeyPreconditionStatements}

    int affectedRowCount = jdbcTemplate.update(
      SQLQueries.DELETE__${entity.name.upperSnake},
      ps -> {
        ${entity.java8View.preparedStatementSetterStatementsForPK}
      });
  }

  @Override
  public boolean exists(${request.unqualifiedContextClass} context,${entity.java8View.methodArgsForIdFields(false)}) {
    Objects.requireNonNull(context, "context is required and null.");
    ${entity.java8View.primaryKeyPreconditionStatements}

    Integer result = jdbcTemplate.queryForObject(
      SQLQueries.ROW_EXISTS__${entity.name.upperSnake},
      Integer.class,
      ${entity.rdbmsView.jdbcSerializedPrimaryKeyFields});

    return null != result  && 1 == result;
  }

  @Override
  public ${entity.name.upperCamel} findById(${request.unqualifiedContextClass} context,${entity.java8View.methodArgsForIdFields(false)}) {
    Objects.requireNonNull(context, "context is required and null.");
    ${entity.java8View.primaryKeyPreconditionStatements}

    List<${entity.name.upperCamel}> results = jdbcTemplate.query(
        SQLQueries.SELECT_BY_PK__${entity.name.upperSnake},
        ps -> {
          ${entity.java8View.preparedStatementSetterStatementsForPK}
        },
        rowMapper);

    // Invariant: results is never null
    if (results.isEmpty()) {
      return null;
    }

    if (results.size() > 1) {
      //TODO: include PK arg(s)
      throw new IllegalStateException("Multiple rows match the PK: context=" + context + ", results=" + results);
    }

    return results.get(0);
  }
  </#if>

  @Override
  public void create(${request.unqualifiedContextClass} context,${entity.name.upperCamel} entity) {
    Objects.requireNonNull(context, "context is required and null.");
    Objects.requireNonNull(entity, "entity is required and null.");

    int affectedRowCount = jdbcTemplate.update(
      SQLQueries.INSERT__${entity.name.upperSnake},
      ps -> {
        try {
            ${entity.java8View.insertPreparedStatementSetterStatements}

        } catch (Exception ex) {
          throw new RuntimeException("Failed to create ${entity.name.upperCamel}: " +
            "context=" + context +
            ", entity=" + entity,
            ex);
        }
    });
  }

  @Override
  public List<${entity.name.upperCamel}> list(${request.unqualifiedContextClass} context) {
    Objects.requireNonNull(context, "context is required and null.");

    return Collections.unmodifiableList(
      jdbcTemplate.query(
        SQLQueries.SELECT_ALL__${entity.name.upperSnake},
        rowMapper));
  }

  @Override
  public void update(${request.unqualifiedContextClass} context,${entity.name.upperCamel} entity) {
    Objects.requireNonNull(context, "context is required and null.");
    Objects.requireNonNull(entity, "entity is required and null.");

    int affectedRowCount = jdbcTemplate.update(
      SQLQueries.UPDATE__${entity.name.upperSnake},
      ps -> {
        try {
            ${entity.java8View.updatePreparedStatementSetterStatements}

        } catch (Exception ex) {
          throw new RuntimeException("Failed to update ${entity.name.upperCamel}: " +
            "context=" + context +
            ", entity=" + entity,
            ex);
        }
    });
  }

  @Override
  public void upsert(${request.unqualifiedContextClass} context,${entity.name.upperCamel} entity) {
    Objects.requireNonNull(context, "context is required and null.");
    Objects.requireNonNull(entity, "entity is required and null.");

    //TODO: more here
  }

  // -- Patch methods
<#list entity.nonIdFields as field>
  @Override
  public void set${field.name.upperCamel}(
    ${request.unqualifiedContextClass} context,
    ${entity.java8View.methodArgsForIdFields(false)},
    ${field.java8View.unqualifiedType} ${field.name.lowerCamel}) {
    Objects.requireNonNull(context, "context is required and null.");
    ${entity.java8View.primaryKeyPreconditionStatements}
    <#if field.type.nullable>
    //TODO: requireNonNull precondition on ${field.java8View.unqualifiedType} (except for primitives)
    </#if>

    //TODO: field validation here (since not part of the POJO validation)

    int affectedRowCount = jdbcTemplate.update(
      SQLQueries.PATCH__${entity.name.upperSnake}__${field.name.upperSnake},
      ps -> {
        try {
          ${field.java8View.updateFieldPreparedStatementSetterStatements(entity.idFields)}

        } catch (Exception ex) {
          throw new RuntimeException("Failed to patch ${entity.name.upperCamel}.${field.name.lowerCamel}: " +
            "context=" + context + ", value=" + ${field.name.lowerCamel},
            ex);
        }
    });
  }

</#list>
}
