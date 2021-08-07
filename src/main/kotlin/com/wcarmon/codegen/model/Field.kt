package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.util.*

/**
 * See src/main/resources/json-schema/field.schema.json
 *
 * Represents ...
 * - REST: resource property/attribute
 * - Protocol buffer: field
 * - RDBMS: column
 *
 * - C++: struct member, class data member
 * - C: struct member
 * - Golang: struct field
 * - Java: class field, record field
 * - Kotlin: class field
 * - Rust: struct field
 * - Typescript: property
 *
 * See extensions package for laguage & framework specific methods
 */
// `$id` and `$schema` are part of json standard, but not useful for code generation
@JsonIgnoreProperties("\u0024schema", "\u0024id")
@JsonPropertyOrder(alphabetic = true)
data class Field(
  val name: Name,

  val type: LogicalFieldType,

  val defaultValue: String? = null,

  val documentation: Documentation = Documentation.EMPTY,

  val rdbms: RDBMSColumn? = null,

  val validation: FieldValidation = FieldValidation(),
) {

  companion object {

    @JvmStatic
    @JsonCreator
    fun parse(
      @JsonProperty("defaultValue") defaultValue: String? = null,
      @JsonProperty("documentation") documentation: Documentation = Documentation.EMPTY,
      @JsonProperty("enumType") enumType: Boolean = false,
      @JsonProperty("jvmDeserializerTemplate") jvmDeserializerTemplate: String = "",
      @JsonProperty("jvmSerializerTemplate") jvmSerializerTemplate: String = "",
      @JsonProperty("name") name: Name,
      @JsonProperty("nullable") nullable: Boolean = false,
      @JsonProperty("precision") precision: Int = 0,
      @JsonProperty("rdbms") rdbms: RDBMSColumn? = null,
      @JsonProperty("scale") scale: Int = 0,
      @JsonProperty("signed") signed: Boolean = true,
      @JsonProperty("type") typeLiteral: String = "",
      @JsonProperty("typeParameters") typeParameters: List<String> = listOf(),
      @JsonProperty("validation") validation: FieldValidation = FieldValidation(),
    ): Field {

      //TODO: missing context
      require(typeLiteral.isNotBlank()) { "Field.type is required" }

      //TODO: signed should override whatever is specified on type literal

      return Field(
        defaultValue = defaultValue,
        documentation = documentation,
        name = name,
        rdbms = rdbms,
        type = LogicalFieldType(
          base = BaseFieldType.parse(typeLiteral),
          enumType = enumType,
          jvmDeserializerTemplate = jvmDeserializerTemplate,
          jvmSerializerTemplate = jvmSerializerTemplate,
          nullable = nullable,
          precision = precision,
          rawTypeLiteral = typeLiteral,
          scale = scale,
          signed = signed,
          typeParameters = typeParameters,
        ),
        validation = validation,
      )
    }
  }

  // -- Language & Framework specific
  val asJava by lazy {
    asJava(type)
  }

  val collection by lazy {
    type.base.isCollection
  }

  val isPrimaryKeyField by lazy {
    rdbms?.positionInPrimaryKey ?: -1 >= 0
  }

  //TODO: move to jackson extensions file
  val jacksonTypeRef by lazy {
    require(type.base.isParameterized) {
      "type references are only required for parameterized types"
    }

    when (type.base) {
      LIST -> "List<${type.typeParameters[0]}>"
      SET -> "Set<${type.typeParameters[0]}>"
      else -> TODO("Build TypeReference for $this")
    }
  }

  val jdbcGetter by lazy {
    jdbcGetter(type)
  }

  val newJavaCollectionExpression by lazy {
    newJavaCollectionExpression(type.base)
  }

  val shouldQuoteInString by lazy {
    when (type.base) {
      STRING -> true
      else -> false
    }
  }

  val shouldUseJVMDeserializer by lazy {
    shouldUseJVMDeserializer(type)
  }

  val unmodifiableJavaCollectionMethod by lazy {
    unmodifiableJavaCollectionMethod(type.base)
  }

  fun asPostgreSQL(varcharLength: Int = 0) {
    asPostgreSQL(type, varcharLength)
  }

  fun javaEqualityExpression(
    identifier0: String,
    identifier1: String,
  ) = com.wcarmon.codegen.model.util.javaEqualityExpression(
    type,
    name,
    identifier0,
    identifier1
  )

  fun jvmDeserializeTemplate(fieldValueExpression: String) =
    jvmDeserializeTemplate(type, fieldValueExpression)
}
