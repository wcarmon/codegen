package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
  entity.kotlinView.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}

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

  /** To set timestamp on patch methods */
  private val clock: Clock,
<#-- -->
  private val jdbcTemplate: JdbcTemplate,
<#-- -->
  <#if entity.jvmView.requiresObjectWriter>
  /** To serialize collection fields */
  private val objectWriter: ObjectWriter,
  </#if>
<#-- -->
  private val rowMapper: RowMapper<${entity.name.upperCamel}>,
): ${entity.name.upperCamel}DAO {

<#if entity.hasIdFields>
  override fun delete(context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)}) {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    jdbcTemplate.update(DELETE__${entity.name.upperSnake}) { ps ->
        ${entity.kotlinView.preparedStatementSetterStatementsForPK}
    }
  }

  override fun exists(context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)}): Boolean {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    return 1 == jdbcTemplate.queryForObject(
      ROW_EXISTS__${entity.name.upperSnake},
      Int::class.java,
      ${entity.rdbmsView.jdbcSerializedPrimaryKeyFields})
  }

  override fun findById(context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)}): ${entity.name.upperCamel}? {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    val results =
      jdbcTemplate.query(
        SELECT_BY_PK__${entity.name.upperSnake},
        PreparedStatementSetter { ps ->
          ${entity.kotlinView.preparedStatementSetterStatementsForPK}
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

  override fun create(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {

    val affectedRowCount = jdbcTemplate.update(
      INSERT__${entity.name.upperSnake},
    ) { ps ->
        ${entity.kotlinView.insertPreparedStatementSetterStatements}
      }

    check(affectedRowCount == 1){ "1-row should have been inserted for entity=${r"$"}entity" }
  }

  override fun list(context: ${request.jvmView.unqualifiedContextClass}): List<${entity.name.upperCamel}> =
    jdbcTemplate.query(
      SELECT_ALL__${entity.name.upperSnake},
      rowMapper
    )

<#if entity.hasNonIdFields>
  override fun update(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {

    jdbcTemplate.update(
      UPDATE__${entity.name.upperSnake}
    ) { ps ->
        ${entity.kotlinView.updatePreparedStatementSetterStatements}
    }
  }

</#if>
  override fun upsert(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {
    TODO("finish this method")
  }

  <#list entity.patchableFields as field>
    // -- Patch methods
    override fun set${field.name.upperCamel}(
      context: ${request.jvmView.unqualifiedContextClass},
      ${entity.kotlinView.methodArgsForIdFields(false)},
      ${field.name.lowerCamel}: ${field.kotlinView.unqualifiedType}) {

      //TODO: '${field.name.lowerCamel}' field validation here (since not part of the POJO validation)

      jdbcTemplate.update(
        PATCH__${entity.name.upperSnake}__${field.name.upperSnake}
      ) { ps ->
        ${field.kotlinView.updateFieldPreparedStatementSetterStatements(
          entity.idFields,
          entity.updatedTimestampField)}
      }
    }

  </#list>
}
