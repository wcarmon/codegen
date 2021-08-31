package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
  request.java8View.importsForFieldsOnAllEntities(entities),
  request.jvmView.contextClass,
  request.jvmView.extraImports)}

/**
 * RowMapper Beans
 * <p>
 * See {@link org.springframework.jdbc.core.RowMapper}
 */
@Configuration
public class RowMapperBeans {

<#list entities as entity>
  @Bean
  <#if entity.jvmView.requiresObjectReader>
  RowMapper<${entity.name.upperCamel}> ${entity.name.lowerCamel}RowMapper(ObjectMapper objectMapper) {
    return new ${entity.name.upperCamel}RowMapper(objectMapper);
<#-- -->
  <#else>
  RowMapper<${entity.name.upperCamel}> ${entity.name.lowerCamel}RowMapper() {
    return new ${entity.name.upperCamel}RowMapper();
  </#if>
  }

</#list>
}
