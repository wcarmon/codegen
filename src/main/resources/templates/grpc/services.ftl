<#--
Generates proto file
To make java classes, use: ./gradlew generateProto
-->
syntax = "proto3";

import "entities.proto";
import "request-response.proto";

//TODO: more here

service Foo {
// Sends a greeting
<#--  rpc SayHelloz (CheeseRequest) returns (CheeseReply) {}-->
}

