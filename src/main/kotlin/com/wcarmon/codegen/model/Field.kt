package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wcarmon.codegen.DEBUG_MODE
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.util.getPostgresTypeLiteral
import com.wcarmon.codegen.view.*

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

  /**
   * Might be (logically) overriden by tech specific config below
   */
  val type: LogicalFieldType,

  /**
   * always false for id fields
   */
  val canUpdate: Boolean = true,

  val canLog: Boolean = true,

  val defaultValue: DefaultValue = DefaultValue(),

  /** No leading comment markers (no leading slashes, no leading asterisk) */
  val documentation: List<String> = listOf(),

  /**
   * null:  not a Id/PK field
   * 0:     1st part of Id/PK field
   * 1:     2nd part of Id/PK field
   * ...
   */
  val positionInId: Int? = null,
  val validationConfig: FieldValidation = FieldValidation(),

  // -- Technology specific config
  private val golangConfig: GolangFieldConfig = GolangFieldConfig(),
  private val jvmConfig: JVMFieldConfig = JVMFieldConfig(),
  private val protobufConfig: ProtoBufFieldConfig = ProtoBufFieldConfig(),

  //TODO: mark private
  val rdbmsConfig: RDBMSColumnConfig = RDBMSColumnConfig(),
) {

  companion object {

    @JvmStatic
    @JsonCreator
    fun parse(
      @JsonProperty("canLog") canLog: Boolean?,
      @JsonProperty("canUpdate") canUpdate: Boolean?,
      @JsonProperty("defaultValue") defaultValue: DefaultValue?,
      @JsonProperty("documentation") documentation: Iterable<String>?,
      @JsonProperty("enumType") enumType: Boolean?,
      @JsonProperty("golang") golangConfig: GolangFieldConfig?,
      @JsonProperty("jvm") jvmFieldConfig: JVMFieldConfig?,
      @JsonProperty("name") name: Name,
      @JsonProperty("nullable") nullable: Boolean?,
      @JsonProperty("positionInId") positionInId: Int?,
      @JsonProperty("precision") precision: Int?,
      @JsonProperty("protobuf") protobufConfig: ProtoBufFieldConfig?,
      @JsonProperty("rdbms") rdbmsConfig: RDBMSColumnConfig?,
      @JsonProperty("scale") scale: Int?,
      @JsonProperty("signed") signed: Boolean?,
      @JsonProperty("type") typeLiteral: String?,
      @JsonProperty("validation") validationConfig: FieldValidation?,
    ): Field {

      require(typeLiteral?.isNotBlank() ?: false) {
        "Field.type is required: this=$this, name=$name"
      }

      //TODO: signed should override whatever is specified on type literal

      val logicalType = LogicalFieldType(
        base = BaseFieldType.parse(typeLiteral ?: ""),
        enumType = enumType ?: false,
        nullable = nullable ?: false,
        precision = precision,
        rawTypeLiteral = typeLiteral ?: "",
        scale = scale ?: 0,
        signed = signed ?: true,
      )

      return Field(
        canLog = canLog ?: true,
        canUpdate = canUpdate ?: true,
        defaultValue = defaultValue ?: DefaultValue(),
        documentation = documentation?.toList() ?: listOf(),
        golangConfig = golangConfig ?: GolangFieldConfig(),
        jvmConfig = jvmFieldConfig ?: JVMFieldConfig(),
        name = name,
        positionInId = positionInId,
        protobufConfig = protobufConfig ?: ProtoBufFieldConfig(),
        rdbmsConfig = rdbmsConfig ?: RDBMSColumnConfig(),
        type = logicalType,
        validationConfig = validationConfig ?: FieldValidation(),
      )
    }
  }

  init {

    val isIdField = (positionInId ?: -1) >= 0
    if (isIdField) {
      require(!type.nullable) {
        "Id/PrimaryKey fields cannot be nullable: $this"
      }
    }

    //NOTE: precision and scale are validated on LogicalFieldType

    if (positionInId != null) {
      require(positionInId >= 0) {
        "positionInId must be non-negative: $positionInId, this=$this"
      }
    }

    assertTypeParametersValid(GOLANG_1_8)
    assertTypeParametersValid(JAVA_08)
    assertTypeParametersValid(KOTLIN_JVM_1_4)
    assertTypeParametersValid(PROTOCOL_BUFFERS_3)
  }

  val java8View by lazy {
    Java8FieldView(
      debugMode = DEBUG_MODE,
      field = this,
      jvmView = jvmView,
      rdbmsView = rdbmsView,
      targetLanguage = JAVA_08,
    )
  }

  val kotlinView by lazy {
    KotlinFieldView(
      debugMode = DEBUG_MODE,
      field = this,
      jvmView = jvmView,
      rdbmsView = rdbmsView,
      targetLanguage = KOTLIN_JVM_1_4,
    )
  }

  val golangView by lazy {
    GolangFieldView(
      debugMode = DEBUG_MODE,
      field = this,
      rdbmsView = rdbmsView,
      targetLanguage = GOLANG_1_8,
    )
  }


  val rdbmsView by lazy {
    RDBMSColumnView(
      debugMode = DEBUG_MODE,
      field = this,
    )
  }

  val protoBufView by lazy {
    ProtobufFieldView(
      debugMode = DEBUG_MODE,
      field = this,
      targetLanguage = PROTOCOL_BUFFERS_3,
    )
  }

  val sqlView by lazy {
    RDBMSColumnView(
      debugMode = DEBUG_MODE,
      field = this,
    )
  }

  val sqlDelightView by lazy {
    SQLDelightColumnView(
      debugMode = DEBUG_MODE,
      field = this,
    )
  }

  val isIdField: Boolean = (positionInId ?: -1) >= 0

  val jvmView by lazy {
    JVMFieldView(
      field = this,
      debugMode = DEBUG_MODE)
  }

  fun effectiveBaseType(targetLanguage: TargetLanguage): BaseFieldType =
    when (targetLanguage) {
      GOLANG_1_8 -> golangConfig.overrideBaseType ?: type.base

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> type.base

      KOTLIN_JVM_1_4 -> type.base

      PROTOCOL_BUFFERS_3 -> protobufConfig.overrideBaseType ?: type.base

      SQL_DB2,
      SQL_H2,
      SQL_MARIA,
      SQL_MYSQL,
      SQL_ORACLE,
      SQL_POSTGRESQL,
      SQL_SQLITE,
      -> rdbmsConfig.overrideBaseType ?: type.base

      SQL_DELIGHT -> rdbmsConfig.overrideBaseType ?: type.base

      else -> TODO("Get effective base type for field=$this, targetLanguage=$targetLanguage")
    }

  fun typeParameters(targetLanguage: TargetLanguage): List<String> = when (targetLanguage) {
    //TODO: more here
    else -> TODO("get type params for field=$this, targetLanguage=$targetLanguage")
  }

  /** true for String, Collections, Enums, Arrays */
  fun isParameterized(targetLanguage: TargetLanguage) =
    when (effectiveBaseType(targetLanguage)) {
      ARRAY,
      LIST,
      MAP,
      SET,
      -> true

      USER_DEFINED -> typeParameters(targetLanguage).isNotEmpty()

      else -> false
    }


  fun overrideDefaultValue(targetLanguage: TargetLanguage): DefaultValue =
    when (targetLanguage) {
      //TODO: more here
      else -> TODO("get overrideDefaultValue for field=$this, targetLanguage=$targetLanguage")
    }


  fun typeLiteral(targetLanguage: TargetLanguage): String = when (targetLanguage) {
    //TODO: move logic from views to here

    PROTOCOL_BUFFERS_3 -> protobufConfig.typeLiteral(type)

    SQL_POSTGRESQL -> rdbmsConfig.overrideTypeLiteral ?: getPostgresTypeLiteral(this)

    //TODO: more here
    else -> TODO("get typeLiteral for field=$this, targetLanguage=$targetLanguage")
  }

  fun effectiveRDBMSSerde(targetLanguage: TargetLanguage): Serde {
    when (targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> TODO()

      KOTLIN_JVM_1_4 -> TODO()

      GOLANG_1_8 -> TODO()

      SQL_DB2,
      SQL_DELIGHT,
      SQL_H2,
      SQL_MARIA,
      SQL_MYSQL,
      SQL_ORACLE,
      SQL_POSTGRESQL,
      SQL_SQLITE,
      -> throw UnsupportedOperationException()

      else -> TODO("get effectiveSerde for field=$this, targetLanguage=$targetLanguage")
    }
  }

  fun effectiveProtoSerde(targetLanguage: TargetLanguage): Serde {

    when (targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> TODO()

      KOTLIN_JVM_1_4 -> TODO()

      GOLANG_1_8 -> TODO()

      PROTOCOL_BUFFERS_3 -> throw UnsupportedOperationException()
      else -> TODO("get effectiveSerde for field=$this, targetLanguage=$targetLanguage")
    }
  }

  /**
   * Parametric polymorphism
   */
  private fun assertTypeParametersValid(targetLanguage: TargetLanguage) {

    val typeParameters = typeParameters(targetLanguage)

    when (val n = effectiveBaseType(targetLanguage).requiredTypeParameterCount) {
      0 -> require(typeParameters.isEmpty()) {
        "type parameter not allowed: field=$this"
      }

      1 -> require(typeParameters.size == n) {
        "exactly 1-type parameter required (add 'typeParameters' to Field): field=$this"
      }

      else -> require(typeParameters.size == n) {
        "type parameters required (add 'typeParameters' to Field): " +
            "requiredCount=$n, actualCount=${typeParameters.size}, this=$this"
      }
    }
  }

}
