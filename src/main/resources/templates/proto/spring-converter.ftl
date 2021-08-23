package ${request.packageName.value};

<#list request.extraJVMImports as importable>
import ${importable};
</#list>
import org.springframework.core.convert.converter.Converter;

/**
 * Provides Converter instances suitable for Spring ConversionService
 * <p>
 * See {@link core.convert} package
 * <p>
 * See https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#core-convert
 * See {@link org.springframework.core.convert.ConversionService}
 */
public final class SpringConverters {

  private SpringConverters() {
  }

<#list entities as entity>
  public static final Converter<${entity.name.upperCamel}, ${entity.name.upperCamel}Proto> ${entity.name.upperSnake}_TO_PROTO =
    ProtoPojoConversionUtils::toProto;

  public static final Converter<${entity.name.upperCamel}Proto, ${entity.name.upperCamel}> PROTO_TO_${entity.name.upperSnake} =
      ProtoPojoConversionUtils::fromProto;

</#list>
}
