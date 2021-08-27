<#--
Generates proto file
To make java classes, use: ./gradlew generateProto
-->
syntax = "proto3";

import "entities.proto";
<#list request.extraProtobufImports as importable>
import ${importable};
</#list>

option java_multiple_files = true;
option java_package = "${request.packageName.value}";
option optimize_for = SPEED;

package ${request.packageName.value};

<#list entities as entity>

<#if entity.canCreate>
message Create${entity.name.upperCamel}Request {
  ${entity.name.upperCamel}Proto entity = 1;
}

message Create${entity.name.upperCamel}Response {
  //TODO: generated keys would go here
}
</#if>

<#if entity.canDelete>
message Delete${entity.name.upperCamel}Request {
  //TODO: pk fields here
}

message Delete${entity.name.upperCamel}Response {
}
</#if>

<#if entity.canFindById>
message FindById${entity.name.upperCamel}Request {
  //TODO: pk fields here
}

message FindById${entity.name.upperCamel}Response {
  // at-most-one value
  // proto3 doesn't support optional
  repeated ${entity.name.upperCamel}Proto entity = 1;
}
</#if>

<#if entity.canList>
message List${entity.name.upperCamel}Request {
}

message List${entity.name.upperCamel}Response {
  repeated ${entity.name.upperCamel}Proto entity = 1;
}
</#if>

<#if entity.canUpdate>
message Update${entity.name.upperCamel}Request {
  ${entity.name.upperCamel}Proto entity = 1;
}

message Update${entity.name.upperCamel}Response {
}

  <#list entity.nonPrimaryKeyFields as field>
  //TODO: patch ${entity.name.upperCamel}Proto.${field.name.lowerCamel}
  </#list>
</#if>
</#list>
