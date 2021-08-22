package ${request.packageName.value};

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
<#list request.extraJVMImports as importable>
import ${importable};
</#list>

/**
 * RowMapper Beans
 * <p>
 * See {@link org.springframework.jdbc.core.RowMapper}
 */
@Configuration
public class RowMapperBeans {

<#list entities as entity>
  @Bean
  <#if entity.requiresObjectReader>
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
