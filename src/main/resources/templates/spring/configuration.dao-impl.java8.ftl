package ${request.packageName.value};

import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
<#list request.extraJVMImports as importable>
import ${importable};
</#list>

/**
 * DAO Beans
 */
@Configuration
public class DAOImplBeans {

<#list entities as entity>
  @Bean
  ${entity.name.upperCamel}DAOImpl ${entity.name.lowerCamel}DAOImpl(
      JdbcTemplate jdbcTemplate,
      RowMapper<${entity.name.upperCamel}> rowMapper,
      ObjectWriter objectWriter) {

    return new ${entity.name.upperCamel}DAOImpl(
      jdbcTemplate,
      objectWriter,
      rowMapper);
  }

</#list>
}
