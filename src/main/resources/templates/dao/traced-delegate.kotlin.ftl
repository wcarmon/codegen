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
  override fun delete(context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)}) {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    val span = tracer.buildSpan("jdbc::delete")
      .asChildOf(context.currentSpan())
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      <#list entity.idFields as pk>
      .withTag("${pk.name.lowerCamel}", ${pk.name.lowerCamel}.toString())
      </#list>
      .start()

    val childContext = context.withCurrentSpan(span)
    wrapDAOCall(span) {
      realDAO.delete(childContext, ${entity.jvmView.commaSeparatedIdFieldNames})
    }
  }

  override fun exists(context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)}): Boolean {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    val span = tracer.buildSpan("jdbc::exists")
      .asChildOf(context.currentSpan())
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      <#list entity.idFields as pk>
      .withTag("${pk.name.lowerCamel}", ${pk.name.lowerCamel}.toString())
      </#list>
      .start()

    val childContext = context.withCurrentSpan(span)
    return wrapDAOCall(span) {
      realDAO.exists(childContext, ${entity.jvmView.commaSeparatedIdFieldNames})
    }
  }

  override fun findById(context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)}): ${entity.name.upperCamel}? {
    //TODO: kotlin preconditions on PK fields (see FieldValidation)

    val span = tracer.buildSpan("jdbc::findById")
      .asChildOf(context.currentSpan())
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      <#list entity.idFields as pk>
      .withTag("${pk.name.lowerCamel}", ${pk.name.lowerCamel}.toString())
      </#list>
      .start()

    val childContext = context.withCurrentSpan(span)
    return wrapDAOCall(span) {
      realDAO.findById(childContext, ${entity.jvmView.commaSeparatedIdFieldNames})
    }
  }
</#if>

  override fun create(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {
    val span = tracer.buildSpan("jdbc::create")
      .asChildOf(context.currentSpan())
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      <#list entity.idFields as pk>
      .withTag("${pk.name.lowerCamel}", entity.${pk.name.lowerCamel}.toString())
      </#list>
      .start()

    val childContext = context.withCurrentSpan(span)
    wrapDAOCall(span) {
      realDAO.create(childContext, entity)
    }
  }

  override fun list(context: ${request.jvmView.unqualifiedContextClass}): List<${entity.name.upperCamel}> {
    val span = tracer.buildSpan("jdbc::list")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start()

    val childContext = context.withCurrentSpan(span)
    return wrapDAOCall(span) {
      realDAO.list(childContext)
    }
  }

<#if entity.hasNonIdFields>
  override fun update(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {
    val span = tracer.buildSpan("jdbc::update")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start()

    val childContext = context.withCurrentSpan(span)
    wrapDAOCall(span) {
      realDAO.update(childContext, entity)
    }
  }

</#if>
  override fun upsert(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel}) {
    val span = tracer.buildSpan("jdbc::upsert")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start()

    val childContext = context.withCurrentSpan(span)
    wrapDAOCall(span) {
      realDAO.upsert(childContext, entity)
    }
  }

<#list entity.patchableFields as field>
  // -- Patch methods
  override fun set${field.name.upperCamel}(
    context: ${request.jvmView.unqualifiedContextClass},
    ${entity.kotlinView.methodArgsForIdFields(false)},
    ${field.name.lowerCamel}: ${field.kotlinView.unqualifiedType}) {

    //TODO: '${field.name.lowerCamel}' field validation here (since not part of the POJO validation)

    val span = tracer.buildSpan("jdbc::patch")
      .asChildOf(context.currentSpan())
      .ignoreActiveSpan()
      .withTag("entityType", ENTITY_TYPE_NAME)
      .withTag("fieldName", "${field.name.lowerCamel}")
      .start()

    wrapDAOCall(span) {
      realDAO.set${field.name.upperCamel}(
        context.withCurrentSpan(span),
        ${entity.jvmView.commaSeparatedIdFieldNames},
        ${field.name.lowerCamel}
      )
    }
  }

</#list>

  /** Wrap the DAO call with proper span cleanup */
  private fun <T> wrapDAOCall(span: Span, daoCall: () -> T) =
    try {
      daoCall()

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
