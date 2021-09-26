@file:JvmName("ProtobufMappers")

package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
request.kotlinView.importsForFieldsOnAllEntities(entities),
request.jvmView.contextClass,
request.jvmView.extraImports)}

/*
 * Utils to convert to/from proto entities and domain POJOs
 * <p>
 * All methods are thread-safe, stateless, shareable
 */

<#list entities as entity>
/**
 * @param entity - [${entity.name.upperCamel}] (domain object)
 * @return [${entity.name.upperCamel}Proto] for grpc/protobuf
 */
fun toProto(entity: ${entity.name.upperCamel}): ${entity.name.upperCamel}Proto =
  ${entity.name.upperCamel}Proto.newBuilder()
    ${entity.kotlinView.entityToProtobufSetters}
      .build()

/**
 * @param proto
 * @return [${entity.name.upperCamel}], equivalent to input proto
 */
fun fromProto(proto: ${entity.name.upperCamel}Proto): ${entity.name.upperCamel} =
  ${entity.name.upperCamel}(
    ${entity.kotlinView.protobufToEntitySetters}
  )

</#list>
<#-- -->
  <#list request.getCollectionFields(entities) as fieldWithOwner>
    <#assign field=fieldWithOwner.field>
    <#assign entity=fieldWithOwner.owner>
/**
 * From ${entity.name.upperCamel}::${field.name.lowerCamel} to serialized [List<String>]
 * Protobuf treats [Set] as [List]
 *
 * @param items - ${entity.name.upperCamel}::${field.name.lowerCamel}
 * @return List<Serialized-${field.name.upperCamel}>, possibly empty, never null
 */
fun toStrings(items: Collection<${field.kotlinView.typeParameters[0]}>?): Collection<String> =
  items
      ?.map{ ${field.kotlinView.serializerForTypeParameter(0, "it")} }
      ?.toList() ?: listOf()

/**
 * From (serialized) Proto [Collection] to [Set] for ${entity.name.upperCamel}::${field.name.lowerCamel}
 *
 * @param items - Serialized {@link ${field.kotlinView.typeParameters[0]}} instances
 * @return [Set<${field.kotlinView.typeParameters[0]}>], possibly empty, never null
 */
//TODO: rename based on entity name
fun stringsTo${field.name.upperCamel}Set(items: Collection<String>?): Set<${field.kotlinView.typeParameters[0]}> =
  items
      ?.map{ ${field.kotlinView.deserializerForTypeParameter(0, "it")} }
      ?.toSet() ?: setOf()

/**
 * From (serialized) Proto Collection to {@link List} for ${entity.name.upperCamel}::${field.name.lowerCamel}
 *
 * @param items - Serialized {@link ${field.kotlinView.typeParameters[0]}} instances
 * @return List<${field.kotlinView.typeParameters[0]}>, possibly empty, never null
 */
//TODO: rename based on entity name
fun stringsTo${field.name.upperCamel}List(items: Collection<String>?): List<${field.kotlinView.typeParameters[0]}> =
  items
      ?.map{ ${field.kotlinView.deserializerForTypeParameter(0, "it")} }
      ?.toList() ?: listOf()

</#list>
