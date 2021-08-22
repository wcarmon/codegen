package ${request.packageName.value}

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
<#if request.jvmContextClass?has_content>
import ${request.jvmContextClass}
</#if>
<#list entity.kotlinImportsForFields as importable>
import ${importable}
</#list>
<#list request.extraJVMImports as importable>
import ${importable}
</#list>
<#if entity.requiresObjectWriter>
import com.fasterxml.jackson.databind.ObjectWriter
</#if>
import java.sql.Types


/**
 * DAO implementation for [${entity.pkg.value}.${entity.name.upperCamel}].
 * Uses [org.springframework.jdbc.core.JdbcTemplate] to execute queries.
 * Uses [java.sql.PreparedStatement]
 *
 * See https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html
 * Threadsafe & Reusable after construction
 */
@Suppress("MagicNumber", "TooManyFunctions") // column & placeholder numbers
class ${entity.name.upperCamel}DAOImpl(
  private val jdbcTemplate: JdbcTemplate,

  <#if entity.requiresObjectWriter>
  /** To serialize collection fields */
  private val objectWriter: ObjectWriter,
  </#if>

  private val rowMapper: RowMapper<${entity.name.upperCamel}>,

): ${entity.name.upperCamel}DAO {

<#if entity.hasPrimaryKeyFields>
  override fun delete(context: ${request.unqualifiedContextClass}, ${entity.kotlinMethodArgsForPKFields(false)}) {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    jdbcTemplate.update(DELETE__${entity.name.upperSnake}) { ps ->
        ${entity.kotlinPreparedStatementSetterStatementsForPK}
    }
  }

  override fun exists(context: ${request.unqualifiedContextClass}, ${entity.kotlinMethodArgsForPKFields(false)}): Boolean {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    return 1 == jdbcTemplate.queryForObject(
      ROW_EXISTS__${entity.name.upperSnake},
      Int::class.java,
      ${entity.jdbcSerializedPKFields})
  }

  override fun findById(context: ${request.unqualifiedContextClass}, ${entity.kotlinMethodArgsForPKFields(false)}): ${entity.name.upperCamel}? {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    val results =
      jdbcTemplate.query(
        SELECT_BY_PK__${entity.name.upperSnake},
        PreparedStatementSetter { ps ->
          ${entity.kotlinPreparedStatementSetterStatementsForPK}
        },
        rowMapper)

    if (results.isEmpty()) {
      return null
    }

    if (results.size > 1) {
      //TODO: include PK arg(s)
      throw IllegalStateException("Multiple rows match the PK: context=${r"$"}context, results=${r"$"}results")
    }

    return results[0]
  }
</#if>

  override fun create(context: ${request.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {

    val affectedRowCount = jdbcTemplate.update(
      INSERT__${entity.name.upperSnake},
    ) { ps ->
        ${entity.kotlinInsertPreparedStatementSetterStatements}
      }

    check(affectedRowCount == 1){ "1-row should have been inserted for entity=${r"$"}entity" }
  }

  override fun list(context: ${request.unqualifiedContextClass}): List<${entity.name.upperCamel}> {
    return jdbcTemplate.query(
      SELECT_ALL__${entity.name.upperSnake},
      rowMapper
    )
  }

  override fun update(context: ${request.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {

    jdbcTemplate.update(
      UPDATE__${entity.name.upperSnake}
    ) { ps ->
        ${entity.kotlinUpdatePreparedStatementSetterStatements}
    }
  }

  override fun upsert(context: ${request.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {
    TODO("finish this method")
  }

  // -- Patch methods
  <#list entity.nonPrimaryKeyFields as field>
    override fun set${field.name.upperCamel}(
      context: ${request.unqualifiedContextClass},
      ${entity.kotlinMethodArgsForPKFields(false)},
      ${field.name.lowerCamel}: ${field.unqualifiedKotlinType}) {

      //TODO: '${field.name.lowerCamel}' field validation here (since not part of the POJO validation)

      jdbcTemplate.update(
        PATCH__${entity.name.upperSnake}__${field.name.upperSnake}
      ) { ps ->
        ${entity.kotlinUpdateFieldPreparedStatementSetterStatements(field)}
      }
    }

  </#list>
}
