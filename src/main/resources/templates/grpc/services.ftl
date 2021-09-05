<#--
Generates proto file
To make java classes, use: ./gradlew generateProto

or:
./gradlew clean build generateProto -x detekt -x test -q;

-------------------------------------------------------
Example of using the stub:
```
  val channelBuilder =
    ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext();

  val channel = channelBuilder.build()

  val serviceStub = FooServiceGrpc.newFutureStub(channel)

  val request = CreateFooRequest
    .newBuilder()
    .build()

  val responseFuture = serviceStub.createFoo(request)
```
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
  rpc ${entity.name.upperCamel}Exists(${entity.name.upperCamel}ExistsRequest) returns (${entity.name.upperCamel}ExistsResponse) {}
</#if>
<#-- -->
<#if entity.canFindById>
  rpc FindById${entity.name.upperCamel}(FindById${entity.name.upperCamel}Request) returns (FindById${entity.name.upperCamel}Response) {}
</#if>
<#-- -->
<#if entity.canList>
  rpc List${entity.name.upperCamel}(List${entity.name.upperCamel}Request) returns (List${entity.name.upperCamel}Response) {}
</#if>
<#-- -->
<#if entity.canUpdate>
  rpc Update${entity.name.upperCamel}(Update${entity.name.upperCamel}Request) returns (Update${entity.name.upperCamel}Response) {}

  <#list entity.nonIdFields as field>
  rpc Set${field.name.upperCamel}(Set${entity.name.upperCamel}${field.name.upperCamel}Request) returns (PatchResponse) {}
  </#list>
<#-- TODO: upsert -->
</#if>
}

</#list>
