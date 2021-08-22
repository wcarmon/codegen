package ${request.packageName.value};

import io.opentracing.Tracer;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
<#list request.extraJVMImports as importable>
import ${importable};
</#list>

/**
 * OpenTracing based Traced DAO Beans
 * <p>
 * See {@link io.opentracing.Tracer}
 */
@Configuration
public class TracedDAOBeans {

  @Bean
  Function<${r"Exception"}, String> exceptionSerializer() {
    return Throwable::toString;
  }

<#list entities as entity>
  @Bean
  ${entity.name.upperCamel}DAO ${entity.name.lowerCamel}TracedDAO(
      Tracer tracer,
      Function<${r"Exception"}, String> exceptionSerializer,
      ${entity.name.upperCamel}DAOImpl realDao) {

    return new ${entity.name.upperCamel}TracedDAO(
        realDao,
        tracer,
        exceptionSerializer);
  }

</#list>
}
