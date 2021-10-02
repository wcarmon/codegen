package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
  request.java8View.importsForFieldsOnAllEntities(entities),
  request.jvmView.contextClass,
  request.jvmView.extraImports)}

/**
 * Utils to convert to/from proto entities and domain POJOs
 * All methods are thread-safe, stateless, shareable
 */
public final class ProtobufMappers {

  private ProtobufMappers() {
  }

  <#list entities as entity>
  /**
   * @param entity - {@link ${entity.name.upperCamel}} (domain object)
   * @return {@link ${entity.name.upperCamel}Proto} for grpc/protobuf, or null (for null entity)
   */
  public static ${entity.name.upperCamel}Proto toProto(${entity.name.upperCamel} entity) {
    if (null == entity) {
      return null;
    }

    return ${entity.name.upperCamel}Proto.newBuilder()
      ${entity.java8View.entityToProtobufSetters}
        .build();
  }


  /**
   * @param proto
   * @return {@link ${entity.name.upperCamel}}, equivalent to input proto, or null (for null proto)
   */
  public static ${entity.name.upperCamel} fromProto(${entity.name.upperCamel}Proto proto) {
    if (null == proto) {
      return null;
    }

    try {
      return ${entity.name.upperCamel}.builder()
       ${entity.java8View.protobufToEntitySetters}
          .build();

    } catch (Exception ex) {
      throw new RuntimeException("Failed to build POJO from Proto for ${entity.name.upperCamel}", ex);
    }
  }

  </#list>
}
