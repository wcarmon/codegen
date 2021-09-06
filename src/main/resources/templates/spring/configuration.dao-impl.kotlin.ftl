package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
request.kotlinView.importsForFieldsOnAllEntities(entities),
request.jvmView.contextClass,
request.jvmView.extraImports)}

/** DAO Beans */
@Configuration
open class DAOImplBeans {

<#list entities as entity>
  @Bean
  fun ${entity.name.lowerCamel}DAOImpl(
    clock: Clock,
    jdbcTemplate: JdbcTemplate,
    rowMapper: RowMapper<${entity.name.upperCamel}>,
    objectWriter: ObjectWriter,
  ) =
    ${entity.name.upperCamel}DAOImpl(
      clock = clock,
      jdbcTemplate = jdbcTemplate,
      objectWriter = objectWriter,
      rowMapper = rowMapper,
    )

</#list>
}
