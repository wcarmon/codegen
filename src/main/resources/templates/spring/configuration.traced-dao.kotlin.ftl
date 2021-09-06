package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
  request.kotlinView.importsForFieldsOnAllEntities(entities),
  request.jvmView.contextClass,
  request.jvmView.extraImports)}

/**
 * OpenTracing based Traced DAO Beans
 * See [io.opentracing.Tracer]
 */
@Configuration
open class TracedDAOBeans {

  @Bean
  fun exceptionSerializer(): Function<${r"Exception"}, String> =
    Function(Exception::toString)

<#list entities as entity>
  @Bean
  fun ${entity.name.lowerCamel}TracedDAO(
    tracer: Tracer,
    exceptionSerializer: Function<${r"Exception"}, String>,
    realDao: ${entity.name.upperCamel}DAOImpl,
  ): ${entity.name.upperCamel}DAO =
   ${entity.name.upperCamel}TracedDAO(
      tracer,
      realDao,
      exceptionSerializer,
  )

</#list>
}
