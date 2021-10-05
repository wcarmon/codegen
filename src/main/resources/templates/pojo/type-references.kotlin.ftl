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
 * @param serialized - json version of set data
 * @param objectMapper
 * @param <E> element type
 * @return a new [Set], possibly empty, never null
 */
inline fun <reified E> parseSet(
  serialized: String?,
  objectMapper: ObjectMapper,
): Set<E> =
  if (serialized.isNullOrBlank()) setOf()
  else objectMapper.readValue(serialized)

/**
 * Deserialize to a [List]
 *
 * @param serialized - json version of list data
 * @param objectMapper
 * @param <E> element type
 * @return a new [List], possibly empty, never null
 */
inline fun <reified E> parseList(
  serialized: String?,
  objectMapper: ObjectMapper,
): List<E> =
  if (serialized.isNullOrBlank()) listOf()
  else objectMapper.readValue(serialized)

