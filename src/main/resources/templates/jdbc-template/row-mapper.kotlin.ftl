package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
  entity.kotlinView.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}

/**
 * Maps one row of [ResultSet] data to [${entity.name.upperCamel}] instance
 *
 * See https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/RowMapper.html
 */
class ${entity.name.upperCamel}RowMapper(
<#if entity.jvmView.requiresObjectReader>
  private val objectMapper: ObjectMapper,
</#if>
) : RowMapper<${entity.name.upperCamel}> {

  /**
   * Maps from [ResultSet] to [${entity.name.upperCamel}]
   *
   * @return equivalent [${entity.name.upperCamel}] instance
   */
  @Suppress("UNCHECKED_CAST")
  override fun mapRow(rs: ResultSet, rowNum: Int): ${entity.name.upperCamel} =
    ${entity.name.upperCamel}(
      <#list entity.sortedFieldsWithIdsFirst as field>
      ${field.name.lowerCamel} = ${field.kotlinView.resultSetGetterExpression},
      </#list>
    )
}
