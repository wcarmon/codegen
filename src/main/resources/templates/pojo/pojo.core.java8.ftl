package ${request.packageName.value};

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
<#list entity.javaImportsForFields as importable>
import ${importable};
</#list>
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * Immutable POJO
 * <p>
 * See ${request.prettyTemplateName}
 */
<#-- TODO: include class documentation when present-->
@JsonPropertyOrder(alphabetic = true)
@JsonDeserialize(builder = ${entity.name.upperCamel}.${entity.name.upperCamel}Builder.class)
public final class ${entity.name.upperCamel} {

  <#list entity.sortedFields as field>
<#-- -->
  /**
<#--  TODO: include field documentation when present (move logic to field class)-->
    <#if field.primaryKeyField>
     * Primary key
    </#if>
  */
  private final ${field.java8View.type} ${field.name.lowerCamel};
  </#list>

  private ${entity.name.upperCamel}( ${entity.name.upperCamel}Builder builder ) {
    //TODO: Validation here

    <#list entity.sortedFields as field>
      <#if field.collection>
      this.${field.name.lowerCamel} = ${field.java8View.unmodifiableCollectionMethod}(builder.${field.name.lowerCamel});
      <#else>
      this.${field.name.lowerCamel} = builder.${field.name.lowerCamel};
      </#if>
    </#list>
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ${entity.name.upperCamel} that = (${entity.name.upperCamel}) o;
    <#list entity.sortedFields as field>
      <#if field?is_first>
      return ${field.java8View.equalityExpression("this", "that")}
      <#elseif field?is_last>
        && ${field.java8View.equalityExpression("this", "that")};
<#--      TODO: handle arrays:  Arrays.equals-->
      <#else>
        && ${field.java8View.equalityExpression("this", "that")}
      </#if>
    </#list>
  }

  @Override
  public int hashCode() {
    return Objects.hash(
    <#list entity.sortedFields as field>
      ${field.name.lowerCamel}<#if field?is_last>);<#else>,</#if>
    </#list>
  }

  <#list entity.sortedFields as field>
    public ${field.java8View.type} get${field.name.upperCamel}() {
      return this.${field.name.lowerCamel};
    }

  </#list>

  @Override
  public String toString() {
    return new StringJoiner(", ", ${entity.name.upperCamel}.class.getSimpleName() + "[", "]")
    <#list entity.sortedFields as field>
      <#if field.shouldQuoteInString>
      .add("${field.name.lowerCamel}='" + ${field.name.lowerCamel} + "'")
      <#else>
      .add("${field.name.lowerCamel}=" + ${field.name.lowerCamel})
      </#if>
    </#list>
    .toString();
  }

  public static ${entity.name.upperCamel}Builder builder() {
    return new ${entity.name.upperCamel}Builder();
  }

  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static class ${entity.name.upperCamel}Builder {
<#-- TODO: set default values on fields here-->
    <#list entity.sortedFields as field>
    private ${field.java8View.type} ${field.name.lowerCamel};
    </#list>

    ${entity.name.upperCamel}Builder() {
    }

    <#list entity.sortedFields as field>
    public ${entity.name.upperCamel}Builder ${field.name.lowerCamel}(${field.java8View.type} value) {
      this.${field.name.lowerCamel} = value;
      return this;
    }

      <#if field.collection>

        public ${entity.name.upperCamel}Builder ${field.name.lowerCamel}(${field.type.typeParameters[0]} value) {
          if (this.${field.name.lowerCamel} == null) {
            this.${field.name.lowerCamel} = new ${field.java8View.newCollectionExpression()};
          }

          this.${field.name.lowerCamel}.add(value);
          return this;
        }
      </#if>
    </#list>

    public ${entity.name.upperCamel} build() {
      return new ${entity.name.upperCamel}(this);
    }
  }
}
