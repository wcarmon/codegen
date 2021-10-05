package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
  entity.kotlinView.importsForFields,
  request.extraJVMImports)}


/**
 * DAO Contract for [${entity.pkg.value}.${entity.name.upperCamel}]
 * PK field count: ${entity.idFields?size}
 * Field count: ${entity.fields?size}
 *
 * Assumes coroutines & context passed thru coroutineContext.
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
  suspend fun delete(${entity.kotlinView.methodArgsForIdFields(false)})

  /**
   * @param TODO
   * @return true when [${entity.pkg.value}.${entity.name.upperCamel}] exists with matching PK
   */
  suspend fun exists(${entity.kotlinView.methodArgsForIdFields(false)}): Boolean

  /**
   * @param TODO
   * @return one [${entity.pkg.value}.${entity.name.upperCamel} instance (matching PKs) or null
   */
  suspend fun findById( ${entity.kotlinView.methodArgsForIdFields(false)}): ${entity.name.upperCamel}?
</#if>

  /**
   * Create at-most-one [${entity.pkg.value}.${entity.name.upperCamel}] instance
   */
  suspend fun create(entity: ${entity.name.upperCamel})

  /**
   * @return all {@link ${entity.pkg.value}.${entity.name.upperCamel}} entities or empty List (never null)
   */
  suspend fun list(): List<${entity.name.upperCamel}>

<#if entity.hasNonIdFields>
  /**
   * Update all (non-PK) fields on one [${entity.pkg.value}.${entity.name.upperCamel}] instance
   * (${entity.nonIdFields?size} non-PK fields)
   */
  suspend fun update(entity: ${entity.name.upperCamel})

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
  suspend fun upsert(entity: ${entity.name.upperCamel})

<#list entity.patchableFields as field>
  /**
   * Patch/Set
   *
   * Set one field: [${entity.pkg.value}.${entity.name.upperCamel}#${field.name.lowerCamel}]
   *
   * @param ${field.name.lowerCamel} - replacement for existing value
   */
  suspend fun set${field.name.upperCamel}(
    ${entity.kotlinView.methodArgsForIdFields(false)},
    ${field.name.lowerCamel}: ${field.kotlinView.unqualifiedType}
  )

</#list>
}
