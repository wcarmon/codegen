package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
  request.kotlinView.importsForFieldsOnAllEntities(entities),
  request.jvmView.contextClass,
  request.jvmView.extraImports)}

/**
 * RowMapper Beans
 * See [org.springframework.jdbc.core.RowMapper]
 */
@Configuration
open class RowMapperBeans {

<#list entities as entity>
  @Bean
  <#if entity.jvmView.requiresObjectReader>
  fun ${entity.name.lowerCamel}RowMapper(
    objectMapper: ObjectMapper
  ): RowMapper<${entity.name.upperCamel}> =
    ${entity.name.upperCamel}RowMapper(objectMapper)
<#-- -->
  <#else>
  fun ${entity.name.lowerCamel}RowMapper(): RowMapper<${entity.name.upperCamel}> =
    ${entity.name.upperCamel}RowMapper()
  </#if>

</#list>
}
