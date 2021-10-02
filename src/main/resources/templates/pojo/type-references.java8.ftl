package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
request.extraJVMImports,
request.jvmContextClass)}

/**
 * Jackson TypeReferences & utils for converting collections
 */
public final class TypeReferences {

  private TypeReferences() {
  }

<#list entities as entity>
  ${entity.java8View.typeReferenceDeclarations}
</#list>

  /**
   * Deserialize to a {@link java.util.List}
   *
   * @param serialized json version of list data
   * @param objectMapper
   * @param typeRef
   * @param <T> complete type, (including the List<?>)
   * @return a new {@link java.util.List}, possibly empty, never null
   */
  @SuppressWarnings("unchecked")
  public static <T> T toList(
        String serialized,
        ObjectMapper objectMapper,
        TypeReference<T> typeRef) {
    if (serialized == null || serialized.trim().isEmpty()) {
      return (T) Collections.emptyList();
    }

    Objects.requireNonNull(objectMapper, "objectMapper is required and missing.");
    Objects.requireNonNull(typeRef, "typeRef is required and missing.");

    try {
      return objectMapper.readValue(serialized, typeRef);

    } catch (Exception ex) {
      throw new RuntimeException("Failed to deserialize to List", ex);
    }
  }

  /**
   * Deserialize to a {@link java.util.Set}
   *
   * @param serialized json version of set data
   * @param objectMapper
   * @param typeRef
   * @param <T> complete type, (including the Set<?>)
   * @return a new {@link java.util.Set}, possibly empty, never null
   */
  @SuppressWarnings("unchecked")
  public static <T> T toSet(
      String serialized,
      ObjectMapper objectMapper,
      TypeReference<T> typeRef) {
    if (serialized == null || serialized.trim().isEmpty()) {
      return (T) Collections.emptySet();
    }

    Objects.requireNonNull(objectMapper, "objectMapper is required and missing.");
    Objects.requireNonNull(typeRef, "typeRef is required and missing.");

    try {
      return objectMapper.readValue(serialized, typeRef);

    } catch (Exception ex) {
      throw new RuntimeException("Failed to deserialize to Set", ex);
    }
  }
}
