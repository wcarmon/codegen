package ${request.packageName.value};
${request.jvmView.templateNameComment}

${request.java8View.serializeImports(
  entity.java8View.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}

/**
 * Maps one row of ResultSet data to ${entity.name.upperCamel} instance
 *
 * See https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/RowMapper.html
*/
<#if entity.jvmView.requiresObjectReader>
@SuppressWarnings("unchecked")
</#if>
public final class ${entity.name.upperCamel}RowMapper implements RowMapper<${entity.name.upperCamel}> {

  <#if entity.jvmView.requiresObjectReader>
    <#list entity.collectionFields as field>
    private static final TypeReference<${field.jvmView.jacksonTypeRef}> ${field.name.upperSnake}_TYPE_REF =
      new TypeReference<>(){};
    </#list>

  private final ObjectMapper objectMapper;

  public ${entity.name.upperCamel}RowMapper(ObjectMapper objectMapper) {
    Objects.requireNonNull(objectMapper, "objectMapper is required");

    this.objectMapper = objectMapper;
  }
  <#else>
  public ${entity.name.upperCamel}RowMapper() {}
  </#if>

  /**
   * Maps ${entity.fields?size}-fields from ResultSet
   *
   * @return ${entity.name.upperCamel} instance
   */
  @Override
  public ${entity.name.upperCamel} mapRow(ResultSet rs, int rowNum) throws SQLException {
    Objects.requireNonNull(rs, "null result set passed to ${entity.name.upperCamel}RowMapper");

    return ${entity.name.upperCamel}.builder()
        <#if !entity.idFields?has_content>
        // -- ${entity.commentForPKFields}
        </#if>
        <#list entity.idFields as field>
        .${field.name.lowerCamel}(${field.java8View.resultSetGetterExpression})
        </#list>

        // -- Other Fields
        <#list entity.nonIdFields as field>
        .${field.name.lowerCamel}(${field.java8View.resultSetGetterExpression})
        </#list>
    .build();
  }

  <#if entity.jvmView.requiresObjectReader>
  /**
   * Deserialize to a java.util.List
   *
   * @param serialized json version of list data
   * @param typeRef for compile time type safety
   * @param <L> complete type, (including the List)
   * @return a new List (possibly empty, never null)
   */
  private <L> L toList(String serialized, TypeReference<L> typeRef) {
    if( serialized == null || serialized.trim().isEmpty() ) {
      return (L) Collections.emptyList();
    }

    try {
      return objectMapper.readValue(serialized, typeRef);

    } catch (Exception ex) {
      throw new RuntimeException("Failed to deserialize List: serialized=" + serialized, ex);
    }
  }

  /**
   * Deserialize to a java.util.Set
   *
   * @param serialized json version of set data
   * @param typeRef for compile time type safety
   * @param <S> complete type, (including the Set)
   * @return a new Set (possibly empty, never null)
   */
  private <S> S toSet(String serialized, TypeReference<S> typeRef) {
    if( serialized == null || serialized.trim().isEmpty() ) {
      return (S) Collections.emptySet();
    }

    try {
      return objectMapper.readValue(serialized, typeRef);

    } catch (Exception ex) {
      throw new RuntimeException("Failed to deserialize Set: serialized=" + serialized, ex);
    }
  }
  </#if>
}
