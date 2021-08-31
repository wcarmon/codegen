<#--
Generates proto file
To make java classes, use: ./gradlew generateProto
-->
${request.jvmView.templateDebugInfo}
syntax = "proto3";

option java_multiple_files = true;
option java_package = "${request.packageName.value}";
option optimize_for = SPEED;

package ${request.packageName.value};

<#list entities as entity>

// Entity: ${entity.pkg.value}.${entity.name.upperCamel}
// Field count: ${entity.fields?size}
message ${entity.name.upperCamel}Proto {
<#-- TODO: fix this
  ${entity.protocolBufferFields}
-->
}

</#list>
