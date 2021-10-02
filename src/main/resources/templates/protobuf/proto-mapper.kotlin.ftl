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
