package ${request.packageName.value};

import org.springframework.jdbc.core.RowMapper;
<#if entity.requiresObjectReader>
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
</#if>
<#list request.extraJVMImports as importable>
import ${importable};
</#list>

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;



/**
 * Maps one row of ResultSet data to ${entity.name.upperCamel} instance
 *
 * See https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/RowMapper.html
 * See: ${request.prettyTemplateName}
*/
<#if entity.requiresObjectReader>
@SuppressWarnings("unchecked")
</#if>
public final class ${entity.name.upperCamel}RowMapper implements RowMapper<${entity.name.upperCamel}> {

  <#if entity.requiresObjectReader>
    <#list entity.collectionFields as field>
    private static final TypeReference<${field.jacksonTypeRef}> ${field.name.upperSnake}_TYPE_REF =
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
        <#if !entity.primaryKeyFields?has_content>
        // -- ${entity.commentForPKFields}
        </#if>
        <#list entity.primaryKeyFields as field>
        .${field.name.lowerCamel}(${field.java8View.resultSetGetterExpression})
        </#list>

        // -- Other Fields
        <#list entity.nonPrimaryKeyFields as field>
        .${field.name.lowerCamel}(${field.java8View.resultSetGetterExpression})
        </#list>
    .build();
  }

  <#if entity.requiresObjectReader>
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
