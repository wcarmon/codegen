package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonProperty


//TODO: custom deserializer is simpler than @JsonCreator
fun parse(
  @JsonProperty("canLog") canLog: Boolean = true,
  @JsonProperty("canUpdate") canUpdate: Boolean = true,
  @JsonProperty("documentation") documentation: Iterable<String> = listOf(),
  @JsonProperty("enumType") enumType: Boolean = false,
  @JsonProperty("golang") golangConfig: GolangFieldConfig = GolangFieldConfig(),
  @JsonProperty("jvm") jvmFieldConfig: JVMFieldConfig = JVMFieldConfig(),
  @JsonProperty("name") name: Name,
  @JsonProperty("nullable") nullable: Boolean = false,
  @JsonProperty("positionInId") positionInId: Int? = null,
  @JsonProperty("precision") precision: Int?,
  @JsonProperty("protobuf") protobufConfig: ProtobufFieldConfig = ProtobufFieldConfig(),
  @JsonProperty("rdbms") rdbmsConfig: RDBMSColumnConfig = RDBMSColumnConfig(),
  @JsonProperty("scale") scale: Int = 0,
  @JsonProperty("signed") signed: Boolean = true,
  @JsonProperty("type") typeLiteral: String = "",
  @JsonProperty("validation") validationConfig: FieldValidation = FieldValidation(),
): Field {

  //TODO: allow fieldTemplate (resource url, starts with file:// or classpath://)

  TODO()
}
