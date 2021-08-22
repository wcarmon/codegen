package ${request.packageName.value};

<#list request.extraJVMImports as importable>
import ${importable};
</#list>
import java.util.Objects;

/**
 * Utils to convert to/from proto entities and domain POJOs
 */
public final class ProtoPojoConversionUtils {

  private ProtoPojoConverter() {
  }

  <#list entities as entity>
  //TODO: document me
  public static ${entity.name.upperCamel}Proto toProto(${entity.name.upperCamel} entity) {
    Objects.requireNonNull(entity, "entity is required and missing.");

    //TODO: loop over fields

  }

  public static ${entity.name.upperCamel} fromProto(${entity.name.upperCamel}Proto proto) {
    Objects.requireNonNull(proto, "proto is required and missing.");

    //TODO: loop over fields
  }

  </#list>
}
