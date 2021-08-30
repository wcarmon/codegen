package ${request.packageName.value};

${request.java8View.serializeImports(
  request.java8View.importsForFieldsOnAllEntities(entities),
  request.jvmView.contextClass,
  request.jvmView.extraImports)}

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
