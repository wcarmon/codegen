package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
  entity.kotlinView.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}


private const val ENTITY_TYPE_NAME = "${entity.name.upperCamel}"

/**
 * OpenTracing based Traced DAO
 *
 * Uses Delegation pattern
 *
 * Relies on the Context class (${request.jvmView.unqualifiedContextClass}) to:
 * 1. provide the current [Span]
 * 2. build a new child Context, with a new [Span]
 *
 * Threadsafe & Reusable after construction
 */
@Suppress("TooManyFunctions")
class ${entity.name.upperCamel}TracedDAO(
  private val tracer: Tracer,

  /** Delegation Pattern: All DAO calls are delegated to this */
  private val realDAO: ${entity.name.upperCamel}DAO,

  /** Converts exception to a string for [io.opentracing.Span#setTag] */
  private val exceptionSerializer: Function<${r"Exception"}, String>,

) : ${entity.name.upperCamel}DAO {

<#if entity.hasIdFields>
  override suspend fun delete(${entity.kotlinView.methodArgsForIdFields(false)}) {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    val span = tracer.buildSpan("jdbc::delete")
      .asChildOf(coroutineContext[SpanElement]?.span)
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      <#list entity.idFields as pk>
      .withTag("${pk.name.lowerCamel}", ${pk.name.lowerCamel}.toString())
      </#list>
      .start()

    wrapDAOCall(span) {
      realDAO.delete(${entity.jvmView.commaSeparatedIDFieldNames})
    }
  }

  override suspend fun exists(${entity.kotlinView.methodArgsForIdFields(false)}): Boolean {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    val span = tracer.buildSpan("jdbc::exists")
      .asChildOf(coroutineContext[SpanElement]?.span)
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      <#list entity.idFields as pk>
      .withTag("${pk.name.lowerCamel}", ${pk.name.lowerCamel}.toString())
      </#list>
      .start()

    return wrapDAOCall(span) {
      realDAO.exists(${entity.jvmView.commaSeparatedIDFieldNames})
    }
  }

  override suspend fun findById(${entity.kotlinView.methodArgsForIdFields(false)}): ${entity.name.upperCamel}? {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    val span = tracer.buildSpan("jdbc::findById")
      .asChildOf(coroutineContext[SpanElement]?.span)
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      <#list entity.idFields as pk>
      .withTag("${pk.name.lowerCamel}", ${pk.name.lowerCamel}.toString())
      </#list>
      .start()

    return wrapDAOCall(span) {
      realDAO.findById(${entity.jvmView.commaSeparatedIDFieldNames})
    }
  }
</#if>

  override suspend fun create(entity: ${entity.name.upperCamel}) {
    val span = tracer.buildSpan("jdbc::create")
      .asChildOf(coroutineContext[SpanElement]?.span)
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      <#list entity.idFields as pk>
      .withTag("${pk.name.lowerCamel}", entity.${pk.name.lowerCamel}.toString())
      </#list>
      .start()

    wrapDAOCall(span) {
      realDAO.create(entity)
    }
  }

  override suspend fun list(): List<${entity.name.upperCamel}> {
    val span = tracer.buildSpan("jdbc::list")
        .asChildOf(coroutineContext[SpanElement]?.span)
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start()

    return wrapDAOCall(span) {
      realDAO.list()
    }
  }

  override suspend fun update(entity: ${entity.name.upperCamel}) {
    val span = tracer.buildSpan("jdbc::update")
        .asChildOf(coroutineContext[SpanElement]?.span)
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start()

    wrapDAOCall(span) {
      realDAO.update(entity)
    }
  }

  override suspend fun upsert(entity: ${entity.name.upperCamel}) {
    val span = tracer.buildSpan("jdbc::upsert")
        .asChildOf(coroutineContext[SpanElement]?.span)
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start()

    wrapDAOCall(span) {
      realDAO.upsert(entity)
    }
  }

  // -- Patch methods
<#list entity.nonIdFields as field>
  override suspend fun set${field.name.upperCamel}(
    ${entity.kotlinView.methodArgsForIdFields(false)},
    ${field.name.lowerCamel}: ${field.kotlinView.unqualifiedType}) {

    //TODO: '${field.name.lowerCamel}' field validation here (since not part of the POJO validation)

    val span = tracer.buildSpan("jdbc::patch")
      .asChildOf(coroutineContext[SpanElement]?.span)
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      .withTag("fieldName", "${field.name.lowerCamel}")
      .start()

    wrapDAOCall(span) {
      realDAO.set${field.name.upperCamel}(${entity.jvmView.commaSeparatedIDFieldNames}, ${field.name.lowerCamel})
    }
  }

</#list>

  /** Wrap the DAO call with proper span cleanup */
  private suspend fun <T> wrapDAOCall(span: Span, daoCall: suspend () -> T) =
    try {
      withContext(SpanElement(span)) {
        daoCall()
      }

    } catch (ex: Exception) {
      applyExceptionToSpan(span, ex)
      throw ex

    } finally {
      span.finish()
    }

  private fun applyExceptionToSpan(span: Span, ex: Exception) {
    span.setTag("error", true)
    span.setTag("error.kind", ex.javaClass.name)
    span.setTag("error.object", exceptionSerializer.apply(ex))
  }
}
