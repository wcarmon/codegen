<#--
Generates proto file
To make java classes, use: ./gradlew generateProto
-->
syntax = "proto3";
${request.jvmView.templateDebugInfo}

import "entities.proto";
import "request-response.proto";
<#list request.extraProtobufImports as importable>
import ${importable};
</#list>

option java_multiple_files = true;
option java_package = "${request.packageName.value}";
option optimize_for = SPEED;

package ${request.packageName.value};

<#list entities as entity>
service ${entity.name.upperCamel}Service {
<#if entity.canCreate>
  rpc Create${entity.name.upperCamel}(Create${entity.name.upperCamel}Request) returns (Create${entity.name.upperCamel}Response) {}
</#if>
<#-- -->
<#if entity.canDelete>
  rpc Delete${entity.name.upperCamel}(Delete${entity.name.upperCamel}Request) returns (Delete${entity.name.upperCamel}Response) {}
</#if>
<#-- -->
<#if entity.canCheckForExistence>
<#-- exists -->
</#if>
<#-- -->
<#if entity.canFindById>
<#-- FindbyId -->
</#if>
<#-- -->
<#if entity.canList>
<#-- list -->
</#if>
<#-- -->
<#if entity.canUpdate>
<#-- Patch -->
<#-- Update -->
<#-- TODO: upsert -->
</#if>
}

</#list>
