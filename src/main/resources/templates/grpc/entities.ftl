<#--
Generates proto file
To make java classes, use: ./gradlew generateProto
-->
syntax = "proto3";

option java_multiple_files = true;
option java_package = "${request.packageName.value}";
option optimize_for = SPEED;

package ${request.packageName.value};

<#list entities as entity>

// Entity: ${entity.pkg.value}.${entity.name.upperCamel}
// Field count: ${entity.fields?size}
message ${entity.name.upperCamel}Proto {
  ${entity.protocolBufferFields}
}

</#list>
