@file:JvmName("TypeReferences")
package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
request.extraJVMImports,
request.jvmContextClass)}

<#list entities as entity>
${entity.kotlinView.typeReferenceDeclarations}
</#list>

/**
 * Deserialize to a [Set]
 *
 * @param serialized json version of set data
 * @param <T> complete type, (including the [Set<E>])
 * @return a new [Set], possibly empty, never null
 */
fun <T> toSet(
      serialized: String?,
      objectMapper: ObjectMapper,
      typeRef: TypeReference<T>): T {
  if( serialized == null || serialized.trim().isEmpty() ) {
    @Suppress("UNCHECKED_CAST")
    return setOf<Any>() as T
  }

  return objectMapper.readValue(serialized, typeRef)
}

/**
 * Deserialize to a [List]
 *
 * @param serialized json version of list data
 * @param <T> complete type, (including the [List<E>])
 * @return a new [List], possibly empty, never null
 */
fun <T> toList(
      serialized: String?,
      objectMapper: ObjectMapper,
      typeRef: TypeReference<T>): T {
  if( serialized == null || serialized.trim().isEmpty() ) {
    @Suppress("UNCHECKED_CAST")
    return listOf<Any>() as T
  }

  return objectMapper.readValue(serialized, typeRef)
}
