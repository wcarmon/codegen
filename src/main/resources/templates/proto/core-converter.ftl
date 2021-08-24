package ${request.packageName.value};

<#list request.extraJVMImports as importable>
import ${importable};
</#list>
<#list entities as entity>
<#-- TODO: dedupe -->
  <#list entity.javaImportsForFields as importable>
import ${importable};
  </#list>
</#list>
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Utils to convert to/from proto entities and domain POJOs
 * <p>
 * All methods are thread-safe, stateless, shareable
 */
public final class ProtoPojoConversionUtils {

  private ProtoPojoConversionUtils() {
  }

  <#list entities as entity>
  /**
   * @param entity domain object
   * @return {@link ${entity.name.upperCamel}Proto} for grpc/protobuf, or null (for null entity)
   */
  public static ${entity.name.upperCamel}Proto toProto(${entity.name.upperCamel} entity) {
    if (null == entity) {
      return null;
    }

    return ChronoBoardProto.newBuilder()
      <#list entity.sortedFieldsWithPKFirst as field>
        <#--
        TODO:
        - make expression for ProtoBuilderSetter
        - for java collections, use
            .addAllTags(entity.getTags().stream().map(serde).collect(Collectors.toSet()))
            or use jackson to serialize to json
         -->
        .${field.protoBuilderSetter}(${field.java8View.readForProtoExpression("entity.")})
      </#list>
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

    return ChronoBoard.builder()
    <#list entity.sortedFieldsWithPKFirst as field>
        .${field.name.lowerCamel}(${field.java8View.readFromProtoExpression("proto.")})
    </#list>
        .build();
  }

  </#list>

  <#list getDistinctProtoCollectionFields(entities) as field>
  /**
   * TODO: document me
   * To Proto collection
   */
  private static Collection<String> toStrings(Collection<${field.type.typeParameters[0]}> items) {
    if (items == null || items.isEmpty()) {
      return Collections.emptyList();
    }

    return items
        .stream()
        .map(item -> ${field.java8View.protoSerializeExpressionForTypeParameters[0]})
        .collect(Collectors.toList());
  }

  /**
   * TODO: document me
   * From Proto collection
   */
  private static Set<${field.type.typeParameters[0]}> stringsTo${field.name.upperCamel}Set(Collection<String> items) {
    if (items == null) {
      return Collections.emptySet();
    }

    return items.stream()
        .map(item -> ${field.java8View.protoDeserializeExpressionForTypeParameters[0]})
        .collect(Collectors.toSet());
  }

  /**
   * TODO: document me
   * From Proto collection
   */
  private static List<${field.type.typeParameters[0]}> stringsTo${field.name.upperCamel}List(Collection<String> items) {
    if (items == null) {
      return Collections.emptyList();
    }

    return items.stream()
        .map(item -> ${field.java8View.protoDeserializeExpressionForTypeParameters[0]})
        .collect(Collectors.toList());
  }

  </#list>
}
