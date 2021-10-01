package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
  entity.java8View.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}

${entity.java8View.documentation("Immutable POJO")}
@JsonPropertyOrder(alphabetic = true)
@JsonDeserialize(builder = ${entity.name.upperCamel}.${entity.name.upperCamel}Builder.class)
public final class ${entity.name.upperCamel} {

${entity.java8View.fieldDeclarations}

  private ${entity.name.upperCamel} (${entity.name.upperCamel}Builder builder) {
    <#list entity.sortedFields as field>
      <#-- TODO: use [FieldReadExpression] on the right side of assignment -->
      <#if field.java8View.collection>
      this.${field.name.lowerCamel} = ${field.java8View.unmodifiableCollectionMethod}(builder.${field.name.lowerCamel});
      <#else>
      this.${field.name.lowerCamel} = builder.${field.name.lowerCamel};
      </#if>
    </#list>

    // -- Validation
    ${entity.java8View.validationExpressions}
    ${entity.java8View.interFieldValidationExpressions}
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

<#--  TODO: replace with [EqualityTestExpression]  -->
    ${entity.name.upperCamel} that = (${entity.name.upperCamel}) o;
    <#list entity.sortedFields as field>
      <#if field?is_first>
      return ${field.java8View.equalityExpression("this.${field.name.lowerCamel}", "that.${field.name.lowerCamel}")}
      <#elseif field?is_last>
        && ${field.java8View.equalityExpression("this.${field.name.lowerCamel}", "that.${field.name.lowerCamel}")};
<#--      TODO: handle arrays:  Arrays.equals-->
      <#else>
        && ${field.java8View.equalityExpression("this.${field.name.lowerCamel}", "that.${field.name.lowerCamel}")}
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
    public ${field.java8View.typeLiteral} get${field.name.upperCamel}() {
      return this.${field.name.lowerCamel};
    }

  </#list>

  @Override
  public String toString() {
    return new StringJoiner(", ", ${entity.name.upperCamel}.class.getSimpleName() + "[", "]")
    <#list entity.sortedFields as field>
      <#if field.jvmView.shouldQuoteInString>
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
    ${entity.java8View.fieldDeclarationsForBuilder}

    ${entity.name.upperCamel}Builder() {
    }

    <#list entity.sortedFields as field>
    public ${entity.name.upperCamel}Builder ${field.name.lowerCamel}(${field.java8View.typeLiteral} value) {
      this.${field.name.lowerCamel} = value;
      return this;
    }

      <#if field.java8View.collection>

        public ${entity.name.upperCamel}Builder ${field.name.lowerCamel}(${field.java8View.typeParameters[0]} value) {
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
