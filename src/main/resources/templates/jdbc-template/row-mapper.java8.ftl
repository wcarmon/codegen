package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
  entity.java8View.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}

/**
 * Maps one row of {@link ResultSet} data to {@link ${entity.name.upperCamel}} instance
 *
 * See https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/RowMapper.html
*/
public final class ${entity.name.upperCamel}RowMapper implements RowMapper<${entity.name.upperCamel}> {

  <#if entity.jvmView.requiresObjectReader>
  private final ObjectMapper objectMapper;

  public ${entity.name.upperCamel}RowMapper(ObjectMapper objectMapper) {
    Objects.requireNonNull(objectMapper, "objectMapper is required");

    this.objectMapper = objectMapper;
  }
  <#else>
  public ${entity.name.upperCamel}RowMapper() {}
  </#if>

  /**
   * Maps from {@link ResultSet} to {@link ${entity.name.upperCamel}}
   *
   * @return equivalent {@link ${entity.name.upperCamel}} instance
   */
  @Override
  public ${entity.name.upperCamel} mapRow(ResultSet rs, int rowNum) {
    Objects.requireNonNull(rs, "null ResultSet passed to ${entity.name.upperCamel}RowMapper");

    try {
      return ${entity.name.upperCamel}.builder()
          <#list entity.sortedFieldsWithIdsFirst as field>
          .${field.name.lowerCamel}(${field.java8View.resultSetGetterExpression})
          </#list>
      .build();

    } catch (Exception ex) {
      throw new RuntimeException("Failed to build ${entity.name.upperCamel} from resultSet", ex);
    }
  }
}
