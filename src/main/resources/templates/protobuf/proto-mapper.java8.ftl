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

    return ${entity.name.upperCamel}.builder()
     ${entity.java8View.protobufToEntitySetters}
        .build();
  }

  </#list>
<#-- -->
  <#list request.getCollectionFields(entities) as fieldWithOwner>
    <#assign field=fieldWithOwner.field>
    <#assign entity=fieldWithOwner.owner>
  /**
   * From ${entity.name.upperCamel}::${field.name.lowerCamel} to serialized List<String>
   * <p>
   * Protobuf treats {@link Set} as {@link List}
   *
   * @param items - ${entity.name.upperCamel}::${field.name.lowerCamel}
   * @return List<Serialized-${field.name.upperCamel}>, possibly empty, never null
   */
  public static Collection<String> toStrings(Collection<${field.java8View.typeParameters[0]}> items) {
    if (items == null || items.isEmpty()) {
      return Collections.emptyList();
    }

    return items
        .stream()
        .map(item -> ${field.java8View.serializerForTypeParameter(0, "item")})
        .collect(Collectors.toList());
  }

  /**
   * From (serialized) Proto Collection to Set for ${entity.name.upperCamel}::${field.name.lowerCamel}
   *
   * @param items - Serialized {@link ${field.java8View.typeParameters[0]}} instances
   * @return Set<${field.java8View.typeParameters[0]}>, possibly empty, never null
   */
  //TODO: rename based on entity name
  public static Set<${field.java8View.typeParameters[0]}> stringsTo${field.name.upperCamel}Set(Collection<String> items) {
    if (items == null || items.isEmpty()) {
      return Collections.emptySet();
    }

    return items.stream()
        .map(item -> ${field.java8View.deserializerForTypeParameter(0, "item")})
        .collect(Collectors.toSet());
  }

  /**
   * From (serialized) Proto Collection to {@link List} for ${entity.name.upperCamel}::${field.name.lowerCamel}
   *
   * @param items - Serialized {@link ${field.java8View.typeParameters[0]}} instances
   * @return List<${field.java8View.typeParameters[0]}>, possibly empty, never null
   */
  //TODO: rename based on entity name
  public static List<${field.java8View.typeParameters[0]}> stringsTo${field.name.upperCamel}List(Collection<String> items) {
    if (items == null || items.isEmpty()) {
      return Collections.emptyList();
    }

    return items.stream()
        .map(item -> ${field.java8View.deserializerForTypeParameter(0, "item")})
        .collect(Collectors.toList());
  }

  </#list>
}
