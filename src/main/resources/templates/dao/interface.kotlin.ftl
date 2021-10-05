package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
  entity.kotlinView.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}


/**
 * DAO Contract for [${entity.pkg.value}.${entity.name.upperCamel}]
 * PK field count: ${entity.idFields?size}
 * Field count: ${entity.fields?size}
 *
 * Implementations must be ThreadSafe
 */
@Suppress("TooManyFunctions")
interface ${entity.name.upperCamel}DAO {

<#if entity.hasIdFields>
 /**
  * Delete at-most-one existing [${entity.pkg.value}.${entity.name.upperCamel}] instance
  *
  * NOOP if no matching entity exists
  *
  * @param TODO
  */
<#-- TODO: Add @param to javadoc for context -->
  fun delete(context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)})

  /**
  * @param TODO
  * @return true when [${entity.pkg.value}.${entity.name.upperCamel}] exists with matching PK
  */
<#-- TODO: Add @param to kotlindoc for context  -->
  fun exists(context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)}): Boolean

  /**
  * @param TODO
  * @return one [${entity.pkg.value}.${entity.name.upperCamel} instance (matching PKs) or null
  */
  <#-- TODO: Add @param to kotlindoc for context  -->
  fun findById( context: ${request.jvmView.unqualifiedContextClass}, ${entity.kotlinView.methodArgsForIdFields(false)}): ${entity.name.upperCamel}?
</#if>

  /**
  * Create at-most-one [${entity.pkg.value}.${entity.name.upperCamel}] instance
  */
  fun create(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel})

  /**
  * @return all [${entity.pkg.value}.${entity.name.upperCamel}] entities or empty List (never null)
  */
<#-- TODO: Add @param to kotlindoc for context  -->
  fun list(context: ${request.jvmView.unqualifiedContextClass}): List<${entity.name.upperCamel}>

<#if entity.hasNonIdFields>
  /**
  * Update all (non-PK) fields on one [${entity.pkg.value}.${entity.name.upperCamel}] instance
  * (${entity.nonIdFields?size} non-PK fields)
  */
<#-- TODO: Add @param to kotlindoc for context  -->
  fun update(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel})

</#if>
  /**
  * Upsert/Put [${entity.pkg.value}.${entity.name.upperCamel}]
  *
  * Update if entity exists, Create if entity does not exist
  *
  * Same concept as [java.util.Map#put}
  *
  * @param entity to update or create
  */
<#-- TODO: Add @param to kotlindoc for context  -->
  fun upsert(context: ${request.jvmView.unqualifiedContextClass}, entity: ${entity.name.upperCamel})

<#list entity.patchableFields as field>
 /**
  * Patch/Set
  *
  * Set one field: [${entity.pkg.value}.${entity.name.upperCamel}#${field.name.lowerCamel}]
  *
  * @param ${field.name.lowerCamel} - replacement for existing value
  */
  <#-- TODO: Add @param to kotlindoc for context  -->
  fun set${field.name.upperCamel}(context: ${request.jvmView.unqualifiedContextClass},
    ${entity.kotlinView.methodArgsForIdFields(false)},
    ${field.name.lowerCamel}: ${field.kotlinView.unqualifiedType})

</#list>
}
