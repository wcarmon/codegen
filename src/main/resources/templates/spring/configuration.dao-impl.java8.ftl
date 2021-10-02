package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
  request.java8View.importsForFieldsOnAllEntities(entities),
  request.jvmView.contextClass,
  request.jvmView.extraImports)}

/**
 * DAO Beans
 */
@Configuration
public class DAOImplBeans {

<#list entities as entity>
  @Bean
  ${entity.name.upperCamel}DAOImpl ${entity.name.lowerCamel}DAOImpl(
      Clock clock,
      JdbcTemplate jdbcTemplate,
      <#if entity.jvmView.requiresObjectWriter>
      ObjectWriter objectWriter,
      </#if>
      RowMapper<${entity.name.upperCamel}> rowMapper) {

    return new ${entity.name.upperCamel}DAOImpl(
      clock,
      jdbcTemplate,
      <#if entity.jvmView.requiresObjectWriter>
      objectWriter,
      </#if>
      rowMapper);
  }

</#list>
}
