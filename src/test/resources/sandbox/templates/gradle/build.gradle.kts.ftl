import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
<#if gradleConfig.includeProto>
import com.google.protobuf.gradle.*
</#if>

repositories {
  mavenLocal()
  mavenCentral()

  google()
  gradlePluginPortal()
}

plugins {
  id("java") apply true
  id("org.jetbrains.kotlin.jvm") version "1.5.30-RC" apply true
  id("org.jetbrains.kotlin.plugin.allopen") version "1.5.30-RC" apply true
  id("org.jetbrains.kotlin.plugin.spring") version "1.5.30-RC" apply true

  // for building a CLI application
  application

  /** bootRun task */
  id("org.springframework.boot") version "2.5.3" apply true

<#if gradleConfig.includeSQLDelight>
  id("com.squareup.sqldelight") version "1.5.1" apply true
</#if>

<#if gradleConfig.includeProto>
  // resolves "protobuf"
  id("com.google.protobuf") version "0.8.17" apply true
</#if>

  id("com.github.johnrengelman.shadow") version "7.0.0" apply true
}

apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
apply(plugin = "org.jetbrains.kotlin.plugin.spring")

group = "${gradleConfig.projectGroup}"
version = "${gradleConfig.projectVersion}"

application {
  mainClass.set("${gradleConfig.fullyQualifiedMainClass}")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
  languageVersion = "1.5"
}

compileTestKotlin.kotlinOptions {
  languageVersion = "1.5"
}

dependencies {
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-properties")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-protobuf")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-smile")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
  implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.github.ben-manes.caffeine:caffeine")
  implementation("com.google.guava:guava")
  implementation("com.google.protobuf:protobuf-java")
  implementation("com.h2database:h2")
  implementation("com.konghq:unirest-java")
  implementation("com.konghq:unirest-objectmapper-jackson")
  implementation("com.lmax:disruptor")
  implementation("com.oracle.database.jdbc:ojdbc10")
  implementation("com.squareup.okhttp3:logging-interceptor")
  implementation("com.squareup.okhttp3:okhttp")
  implementation("com.squareup.retrofit2:converter-jackson")
  implementation("com.squareup.retrofit2:retrofit")
  implementation("com.squareup.sqldelight:core")
  implementation("com.squareup.sqldelight:coroutines-extensions")
  implementation("com.squareup.sqldelight:coroutines-extensions-jvm")
  implementation("com.squareup.sqldelight:runtime-jvm")
  implementation("com.squareup.sqldelight:sqlite-driver")
  implementation("com.squareup.wire:wire-compiler")
  implementation("com.squareup.wire:wire-grpc-client")
  implementation("com.squareup.wire:wire-java-generator")
  implementation("com.squareup.wire:wire-kotlin-generator")
  implementation("com.squareup.wire:wire-profiles")
  implementation("com.squareup.wire:wire-runtime")
  implementation("com.squareup.wire:wire-schema")
  implementation("com.squareup:javapoet")
  implementation("com.squareup:kotlinpoet")
  implementation("com.zaxxer:HikariCP")
  implementation("commons-io:commons-io")
  implementation("io.grpc:grpc-alts")
  implementation("io.grpc:grpc-api")
  implementation("io.grpc:grpc-auth")
  implementation("io.grpc:grpc-context")
  implementation("io.grpc:grpc-core")
  implementation("io.grpc:grpc-grpclb")
  implementation("io.grpc:grpc-netty")
  implementation("io.grpc:grpc-okhttp")
  implementation("io.grpc:grpc-protobuf")
  implementation("io.grpc:grpc-stub")
  implementation("io.grpc:protoc-gen-grpc-java")
  implementation("io.lettuce:lettuce-core")
  implementation("io.micrometer:micrometer-core:1.5.5")
  implementation("io.opentracing:opentracing-api")
  implementation("javax.servlet:javax.servlet-api")
  implementation("org.apache.commons:commons-compress")
  implementation("org.apache.commons:commons-lang3")
  implementation("org.apache.commons:commons-text")
  implementation("org.apache.kafka:kafka-clients")
  implementation("org.apache.kafka:kafka-metadata")
  implementation("org.apache.kafka:kafka-raft")
  implementation("org.apache.kafka:kafka-streams")
  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.tomcat:annotations-api")
  implementation("org.apache.velocity.tools:velocity-tools-generic")
  implementation("org.apache.velocity:velocity-engine-core")
  implementation("org.atteo:evo-inflector")
  implementation("org.freemarker:freemarker")
  implementation("org.http4k:http4k-client-apache")
  implementation("org.http4k:http4k-client-okhttp")
  implementation("org.http4k:http4k-contract")
  implementation("org.http4k:http4k-core")
  implementation("org.http4k:http4k-format-jackson")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.koin:koin-core")
  implementation("org.mariadb.jdbc:mariadb-java-client")
  implementation("org.postgresql:postgresql")
  implementation("org.projectlombok:lombok")
  implementation("org.redisson:redisson")
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-log4j2")
  implementation("org.springframework.kafka:spring-kafka")
  implementation("org.springframework.kafka:spring-kafka-test")
  implementation("org.springframework:spring-beans")
  implementation("org.springframework:spring-context")
  implementation("org.springframework:spring-jdbc")
  implementation("org.springframework:spring-web")
  implementation("org.xerial:sqlite-jdbc")
  implementation("org.yaml:snakeyaml")
  implementation("redis.clients:jedis")

  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-jdk8"))

  testImplementation("org.testcontainers:cassandra")
  testImplementation("org.testcontainers:cockroachdb")
  testImplementation("org.testcontainers:couchbase")
  testImplementation("org.testcontainers:database-commons")
  testImplementation("org.testcontainers:db2")
  testImplementation("org.testcontainers:elasticsearch")
  testImplementation("org.testcontainers:gcloud")
  testImplementation("org.testcontainers:jdbc")
  testImplementation("org.testcontainers:jdbc-test")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:kafka")
  testImplementation("org.testcontainers:mariadb")
  testImplementation("org.testcontainers:mongodb")
  testImplementation("org.testcontainers:mysql")
  testImplementation("org.testcontainers:oracle-xe")
  testImplementation("org.testcontainers:postgresql")
  testImplementation("org.testcontainers:r2dbc")
  testImplementation("org.testcontainers:testcontainers")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
  main {
<#if gradleConfig.includeProto>
    proto {
      srcDirs(
        "src/gen/proto",
        "src/main/proto"
      )
    }
</#if>

    java.srcDirs(
<#if gradleConfig.includeProto>
      "build/generated/source/proto/main/grpc",
      "build/generated/source/proto/main/java",
</#if>
      "src/gen/java",
      "src/gen/kotlin",
      "src/main/java",
      "src/main/kotlin"
    )
  }

  test {
    java.srcDirs(
<#if gradleConfig.includeProto>
      "build/generated/source/proto/main/grpc",
      "build/generated/source/proto/main/java",
</#if>
      "src/gen/java",
      "src/gen/kotlin",
      "src/main/java",
      "src/main/kotlin",
      "src/test/java",
      "src/test/kotlin"
    )
  }

  create("intTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
  }
}


tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}


tasks.withType<Test>().configureEach {

  failFast = true
  useJUnitPlatform { }

  testLogging {
    events("passed", "skipped", "failed", "standardOut", "standardError")
    showExceptions = true
    showStandardStreams = true
  }
}


tasks.withType<JavaCompile>().configureEach {
  options.isDebug = false
  options.isFailOnError = true
  options.isFork = true
  options.isIncremental = true
  options.isVerbose = false
}


<#if gradleConfig.includeSQLDelight>
sqldelight {

  database("SandboxDatabase") {
    dialect = "sqlite:3.24"
    packageName = "io.wc.codegen.sandbox.sql"
    schemaOutputDirectory = file("build/db")
    sourceFolders = listOf("sqldelight", "db")
  }

  linkSqlite = false
}
</#if>

<#if gradleConfig.includeProto>
apply(from = "protobuf.gradle")
protobuf {

  // -- Resolves references in *.proto
  // ProtobufConfigurator.protoc
  protoc {
    artifact = "com.google.protobuf:protoc:3.17.3"
  }

  // -- Builds the services (rpc declarations)
  // ProtobufConfigurator.generateProtoTasks
  generateProtoTasks {
    ofSourceSet("main").forEach {
      it.plugins {
        id("grpc")
      }
    }
  }
}
</#if>

configurations.all {
  resolutionStrategy.failOnVersionConflict()

  exclude(group = "ch.qos.logback", module = "logback-classic")
  exclude(group = "ch.qos.logback", module = "logback-core")
  exclude(group = "com.google.code.findbugs", module = "jsr305")
  exclude(group = "commons-logging", module = "commons-logging")
  exclude(group = "hibernate-validator", module = "org.hibernate.validator")
  exclude(group = "javax.annotation", module = "jsr305")
  exclude(group = "jboss-logging", module = "org.jboss.logging")
  exclude(group = "jta", module = "jta")
  exclude(group = "log4j", module = "log4j")
  exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
  exclude(group = "org.nd4j", module = "jackson")
  exclude(group = "org.nd4j", module = "nd4j-common")
  exclude(group = "org.nd4j", module = "protobuf")
  exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
  exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
  exclude(group = "osworkflow", module = "osworkflow")
  exclude(group = "webwork", module = "pell-multipart-request")

  resolutionStrategy.force(
    "ch.qos.logback.contrib:logback-jackson:0.1.5",
    "ch.qos.logback:logback-access:1.2.3",
    "ch.qos.logback:logback-classic:1.2.3",
    "ch.qos.logback:logback-core:1.2.3",
    "colt:colt:1.2.0",
    "com.amazonaws:aws-android-sdk-core:2.19.4",
    "com.amazonaws:aws-java-sdk-dynamodb:1.11.918",
    "com.amazonaws:aws-java-sdk-iam:1.11.918",
    "com.amazonaws:aws-java-sdk-lambda:1.11.918",
    "com.amazonaws:aws-java-sdk-s3:1.11.918",
    "com.amazonaws:aws-lambda-java-core:1.2.1",
    "com.amazonaws:aws-lambda-java-events:3.6.0",
    "com.atlassian.annotations:atlassian-annotations:0.16",
    "com.atlassian.cache:atlassian-cache-api:2.8.3",
    "com.atlassian.core:atlassian-core:5.0.3",
    "com.atlassian.event:atlassian-event:3.0.0",
    "com.atlassian.fugue:fugue:2.5.0",
    "com.atlassian.jira:jira-api:7.1.0-QR20151229171111",
    "com.atlassian.jira:jira-rest-java-client-core:5.2.2",
    "com.atlassian.plugins:atlassian-plugins-webfragment-api:4.0.0-m002",
    "com.atlassian.profiling:atlassian-profiling:1.9",
    "com.atlassian.sal:sal-api:3.0.3",
    "com.atlassian.util.concurrent:atlassian-util-concurrent:3.0.0",
    "com.auth0:java-jwt:3.11.0",
    "com.dlsc.formsfx:formsfx-core:11.4.1",
    "com.dlsc.preferencesfx:preferencesfx-core:11.7.0",
    "com.dooapp.fxform2:core:9.0.0",
    "com.dooapp.fxform2:extensions:9.0.0",
    "com.esotericsoftware:kryo:5.2.0",
    "com.fasterxml.jackson.core:jackson-annotations:2.12.4",
    "com.fasterxml.jackson.core:jackson-core:2.12.4",
    "com.fasterxml.jackson.core:jackson-databind:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-properties:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-protobuf:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-smile:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.2",
    "com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.4",
    "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.4",
    "com.fasterxml.jackson.module:jackson-module-jsonSchema:2.12.4",
    "com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4",
    "com.fasterxml.jackson.module:jackson-module-parameter-names:2.12.4",
    "com.fasterxml.jackson.module:jackson-module-scala_2.13:2.12.4",
    "com.fasterxml.jackson:jackson-bom:2.12.4",
    "com.github.ben-manes.caffeine:caffeine:2.8.5",
    "com.github.javafaker:javafaker:1.0.2",
    "com.google.api.grpc:proto-google-common-protos:2.0.0",
    "com.google.api.grpc:proto-google-iam-v1:1.0.1",
    "com.google.api:api-common:1.10.1",
    "com.google.api:gax-grpc:1.60.0",
    "com.google.api:gax:1.60.0",
    "com.google.auth:google-auth-library-credentials:0.22.1",
    "com.google.auth:google-auth-library-oauth2-http:0.22.0",
    "com.google.auto.value:auto-value-annotations:1.7.4",
    "com.google.cloud.functions.invoker:java-function-invoker:1.0.0-alpha-2-rc5",
    "com.google.cloud.functions:functions-framework-api:1.0.2",
    "com.google.cloud:google-cloud-core-grpc:1.93.10",
    "com.google.cloud:google-cloud-core:1.93.9",
    "com.google.cloud:google-cloud-firestore:2.1.0",
    "com.google.cloud:google-cloud-logging-logback:0.118.5-alpha",
    "com.google.cloud:google-cloud-logging:2.0.1",
    "com.google.cloud:google-cloud-storage:1.113.4",
    "com.google.cloud:google-cloud-trace:1.2.6",
    "com.google.code.gson:gson:2.8.6",
    "com.google.errorprone:error_prone_annotations:2.4.0",
    "com.google.guava:guava:30.0-jre",
    "com.google.http-client:google-http-client-jackson2:1.37.0",
    "com.google.http-client:google-http-client:1.37.0",
    "com.google.protobuf:protobuf-gradle-plugin:0.8.17",
    "com.google.protobuf:protobuf-java:3.13.0",
    "com.google.protobuf:protobuf-kotlin:3.17.3",
    "com.google.protobuf:protoc:3.17.3",
    "com.graphql-java:graphql-java:17.1",
    "com.h2database:h2:1.4.200",
    "com.ibm.db2:jcc:11.5.6.0",
    "com.konghq:unirest-java:3.11.03",
    "com.konghq:unirest-objectmapper-jackson:3.11.03",
    "com.lmax:disruptor:3.4.4",
    "com.mchange:c3p0:0.9.5.5",
    "com.oracle.database.jdbc:ojdbc10:19.11.0.0",
    "com.sparkjava:spark-core:2.9.3",
    "com.squareup.moshi:moshi-adapters:1.12.0",
    "com.squareup.moshi:moshi-kotlin-codegen:1.12.0",
    "com.squareup.moshi:moshi-kotlin-tests:1.10.0",
    "com.squareup.moshi:moshi-kotlin:1.12.0",
    "com.squareup.moshi:moshi:1.12.0",
    "com.squareup.okhttp3:logging-interceptor:4.9.0",
    "com.squareup.okhttp3:okhttp:4.9.0",
    "com.squareup.okio:okio:2.8.0",
    "com.squareup.picasso:picasso:2.71828",
    "com.squareup.retrofit2:converter-jackson:2.9.0",
    "com.squareup.retrofit2:retrofit:2.9.0",
    "com.squareup.sqldelight:android-driver:1.5.1",
    "com.squareup.sqldelight:core:1.5.1",
    "com.squareup.sqldelight:coroutines-extensions-jvm:1.5.1",
    "com.squareup.sqldelight:coroutines-extensions:1.5.1",
    "com.squareup.sqldelight:runtime-jvm:1.5.1",
    "com.squareup.sqldelight:sqlite-driver:1.5.1",
    "com.squareup.wire:wire-compiler:3.7.0",
    "com.squareup.wire:wire-grpc-client:3.7.0",
    "com.squareup.wire:wire-java-generator:3.7.0",
    "com.squareup.wire:wire-kotlin-generator:3.7.0",
    "com.squareup.wire:wire-profiles:3.7.0",
    "com.squareup.wire:wire-runtime:3.7.0",
    "com.squareup.wire:wire-schema:3.7.0",
    "com.squareup:javapoet:1.13.0",
    "com.squareup:kotlinpoet:1.9.0",
    "com.squareup:otto:1.3.8",
    "com.thoughtworks.xstream:xstream:1.4.15",
    "com.zaxxer:HikariCP:5.0.0",
    "commons-beanutils:commons-beanutils:1.6.1",
    "commons-codec:commons-codec:1.11",
    "commons-collections:commons-collections:3.2.1",
    "commons-io:commons-io:2.2",
    "commons-lang:commons-lang:2.6",
    "io.atlassian.fugue:fugue:4.7.2",
    "io.debezium:debezium-connector-mysql:1.6.1.Final",
    "io.grpc:grpc-alts:1.40.1",
    "io.grpc:grpc-api:1.40.1",
    "io.grpc:grpc-auth:1.40.1",
    "io.grpc:grpc-context:1.40.1",
    "io.grpc:grpc-core:1.40.1",
    "io.grpc:grpc-grpclb:1.40.1",
    "io.grpc:grpc-kotlin-stub:1.40.1",
    "io.grpc:grpc-netty-shaded:1.40.1",
    "io.grpc:grpc-netty:1.40.1",
    "io.grpc:grpc-okhttp:1.40.1",
    "io.grpc:grpc-protobuf-lite:1.40.1",
    "io.grpc:grpc-protobuf:1.40.1",
    "io.grpc:grpc-stub:1.40.1",
    "io.grpc:protoc-gen-grpc-java:1.40.0",
    "io.jaegertracing:jaeger-client:1.6.0",
    "io.jaegertracing:jaeger-core:1.6.0",
    "io.jaegertracing:jaeger-micrometer:1.6.0",
    "io.jaegertracing:jaeger-proto:0.7.0",
    "io.jaegertracing:jaeger-thrift:1.6.0",
    "io.jaegertracing:jaeger-tracedsl:0.7.0",
    "io.javalin:javalin:3.11.0",
    "io.ktor:ktor-client:1.4.1",
    "io.lettuce:lettuce-core:6.1.4.RELEASE",
    "io.micrometer:micrometer-core:1.5.5",
    "io.micrometer:micrometer-registry-elastic:1.5.5",
    "io.micrometer:micrometer-registry-jmx:1.5.5",
    "io.micrometer:micrometer-registry-prometheus:1.5.5",
    "io.micronaut:micronaut-http:2.5.2",
    "io.micronaut:micronaut-inject:2.5.2",
    "io.micronaut:micronaut-runtime:2.5.2",
    "io.micronaut:micronaut-validation:2.5.2",
    "io.mockk:mockk:1.10.2",
    "io.netty:netty-buffer:4.1.65.Final",
    "io.netty:netty-codec:4.1.65.Final",
    "io.netty:netty-common:4.1.65.Final",
    "io.netty:netty-handler:4.1.65.Final",
    "io.netty:netty-resolver:4.1.65.Final",
    "io.netty:netty-transport-native-epoll:4.1.65.Final",
    "io.netty:netty-transport-native-unix-common:4.1.65.Final",
    "io.netty:netty-transport:4.1.65.Final",
    "io.opentracing:opentracing-api:0.33.0",
    "io.opentracing:opentracing-mock:0.33.0",
    "io.opentracing:opentracing-noop:0.33.0",
    "io.perfmark:perfmark-api:0.23.0",
    "io.projectreactor:reactor-core:3.4.5",
    "io.quarkus:quarkus-jdbc-db2:2.1.2.Final",
    "io.quarkus:quarkus-jdbc-db2:2.1.2.Final",
    "io.swagger.core.v3:swagger-annotations:2.1.9",
    "io.swagger.core.v3:swagger-core:2.1.9",
    "io.swagger.core.v3:swagger-integration:2.1.9",
    "io.swagger.core.v3:swagger-models:2.1.9",
    "javax.cache:cache-api:1.1.1",
    "javax.servlet:javax.servlet-api:3.0.1",
    "javax.validation:validation-api:2.0.1.Final",
    "javax.xml.bind:jaxb-api:2.3.1",
    "jfree:jcommon:1.0.16",
    "joda-time:joda-time:2.7",
    "junit:junit:4.13.1",
    "mysql:mysql-connector-java:8.0.26",
    "net.bytebuddy:byte-buddy-agent:1.10.15",
    "net.bytebuddy:byte-buddy:1.10.15",
    "net.java.dev.jna:jna:5.8.0",
    "net.logstash.logback:logstash-logback-encoder:6.5",
    "net.sf.supercsv:super-csv:2.4.0",
    "no.tornado:tornadofx-controls:1.0.6",
    "no.tornado:tornadofx-controlsfx:0.1.1",
    "no.tornado:tornadofx:1.7.20",
    "opensymphony:propertyset:1.5",
    "org.apache.avro:avro:1.10.2",
    "org.apache.commons:commons-compress:1.21",
    "org.apache.commons:commons-csv:1.9.0",
    "org.apache.commons:commons-lang3:3.12.0",
    "org.apache.commons:commons-math3:3.6.1",
    "org.apache.commons:commons-text:1.9",
    "org.apache.httpcomponents:httpclient-cache:4.5.13",
    "org.apache.httpcomponents:httpclient:4.5.13",
    "org.apache.httpcomponents:httpcore-nio:4.4.13",
    "org.apache.httpcomponents:httpcore:4.4.13",
    "org.apache.httpcomponents:httpmime:4.5.13",
    "org.apache.kafka:connect-json:2.8.0",
    "org.apache.kafka:kafka-clients:2.8.0",
    "org.apache.kafka:kafka-metadata:2.8.0",
    "org.apache.kafka:kafka-raft:2.8.0",
    "org.apache.kafka:kafka-streams:2.8.0",
    "org.apache.kafka:kafka-tools:2.8.0",
    "org.apache.logging.log4j:log4j-api:2.14.0",
    "org.apache.logging.log4j:log4j-core:2.14.0",
    "org.apache.logging.log4j:log4j-jul:2.14.0",
    "org.apache.logging.log4j:log4j-layout-template-json:2.14.0",
    "org.apache.logging.log4j:log4j-slf4j-impl:2.14.0",
    "org.apache.lucene:lucene-analyzers-common:8.7.0",
    "org.apache.lucene:lucene-analyzers:8.7.0",
    "org.apache.lucene:lucene-codecs:8.7.0",
    "org.apache.lucene:lucene-core:8.7.0",
    "org.apache.lucene:lucene-facet:8.7.0",
    "org.apache.lucene:lucene-highlighter:8.7.0",
    "org.apache.lucene:lucene-memory:8.7.0",
    "org.apache.lucene:lucene-queries:8.7.0",
    "org.apache.lucene:lucene-queryparser:8.7.0",
    "org.apache.lucene:lucene-regex:8.7.0",
    "org.apache.lucene:lucene-snowball:8.7.0",
    "org.apache.lucene:lucene-suggest:8.7.0",
    "org.apache.thrift:libthrift:0.14.1",
    "org.apache.tika:tika-core:1.25",
    "org.apache.tika:tika-fuzzing:1.25",
    "org.apache.tika:tika-parsers:1.25",
    "org.apache.tika:tika:1.25",
    "org.apache.tomcat:annotations-api:6.0.53",
    "org.apache.tomcat:tomcat-jdbc:10.0.8",
    "org.apache.velocity.tools:velocity-tools-generic:3.1",
    "org.apache.velocity:velocity-engine-core:2.3",
    "org.assertj:assertj-core:3.20.2",
    "org.assertj:assertj-core:3.20.2",
    "org.atteo:evo-inflector:1.3",
    "org.awaitility:awaitility-kotlin:4.0.3",
    "org.awaitility:awaitility:4.0.3",
    "org.checkerframework:checker-qual:3.4.1",
    "org.codehaus.jackson:jackson-core-asl:1.9.2",
    "org.codehaus.jackson:jackson-mapper-asl:1.9.2",
    "org.codehaus.mojo:animal-sniffer-annotations:1.19",
    "org.conscrypt:conscrypt-openjdk-uber:2.5.1",
    "org.controlsfx:controlsfx:11.1.0",
    "org.ejml:all:0.30",
    "org.elasticsearch.client:elasticsearch-rest-high-level-client:7.9.2",
    "org.elasticsearch:elasticsearch:7.10.1",
    "org.freemarker:freemarker:2.3.31",
    "org.greenrobot:eventbus:3.2.0",
    "org.hdrhistogram:HdrHistogram:2.1.12",
    "org.http4k:http4k-client-apache:4.10.1.0",
    "org.http4k:http4k-client-okhttp:4.10.1.0",
    "org.http4k:http4k-contract:4.10.1.0",
    "org.http4k:http4k-core:4.10.1.0",
    "org.http4k:http4k-format-jackson:4.10.1.0",
    "org.jboss.resteasy:jaxrs-api:3.0.12.Final",
    "org.jboss.resteasy:resteasy-client:4.7.1.Final",
    "org.jboss.resteasy:resteasy-jackson2-provider:4.7.1.Final",
    "org.jboss.resteasy:resteasy-multipart-provider:4.7.1.Final",
    "org.jetbrains.compose.foundation:foundation-desktop:0.4.0-build188",
    "org.jetbrains.compose.runtime:runtime:0.4.0-build188",
    "org.jetbrains.compose.ui:ui-desktop:1.0.0-alpha4-build310",
    "org.jetbrains.compose.ui:ui:0.4.0-build188",
    "org.jetbrains.exposed:exposed-core:0.28.1",
    "org.jetbrains.exposed:exposed-dao:0.28.1",
    "org.jetbrains.exposed:exposed-java-time:0.28.1",
    "org.jetbrains.exposed:exposed-jdbc:0.28.1",
    "org.jetbrains.exposed:exposed:0.17.7",
    "org.jetbrains.kotlin:kotlin-compiler-embeddable:1.5.30-RC",
    "org.jetbrains.kotlin:kotlin-reflect:1.5.30-RC",
    "org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.5.30-RC",
    "org.jetbrains.kotlin:kotlin-stdlib-common:1.5.30-RC",
    "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.5.30-RC",
    "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.30-RC",
    "org.jetbrains.kotlin:kotlin-stdlib:1.5.30-RC",
    "org.jetbrains.kotlin:kotlin-test-junit5:1.5.30-RC",
    "org.jetbrains.kotlin:kotlin-test:1.5.30-RC",
    "org.jetbrains.kotlinx:atomicfu:0.16.2",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.1",
    "org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.0.1:",
    "org.jetbrains:annotations:20.1.0",
    "org.junit.jupiter:junit-jupiter-api:5.7.0",
    "org.junit.jupiter:junit-jupiter-engine:5.7.0",
    "org.junit.jupiter:junit-jupiter-params:5.7.0",
    "org.junit.platform:junit-platform-launcher:1.7.0",
    "org.junit.vintage:junit-vintage-engine:5.7.0",
    "org.koin:koin-android:2.2.0-rc-4",
    "org.koin:koin-core:2.2.0-rc-4",
    "org.kordamp.ikonli:ikonli-antdesignicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-bootstrapicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-boxicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-bpmn-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-captainicon-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-carbonicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-codicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-core:12.2.0",
    "org.kordamp.ikonli:ikonli-coreui-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-dashicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-devicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-elusive-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-entypo-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-evaicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-feather-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-fileicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-fluentui-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-fontawesome5-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-fontelico-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-foundation-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-hawcons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-icomoon-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-ionicons4-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-jamicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-javafx:12.2.0",
    "org.kordamp.ikonli:ikonli-ligaturesymbols-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-lineawesome-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-maki2-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-mapicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-material2-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-materialdesign2-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-medicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-metrizeicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-microns-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-ociicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-octicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-openiconic-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-paymentfont-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-prestashopicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-remixicon-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-runestroicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-simpleicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-simplelineicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-subway-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-themify-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-typicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-unicons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-weathericons-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-websymbols-pack:12.2.0",
    "org.kordamp.ikonli:ikonli-zondicons-pack:12.2.0",
    "org.mariadb.jdbc:mariadb-java-client:2.7.4",
    "org.mock-server:mockserver-client-java:5.11.2",
    "org.mockito:mockito-inline:3.6.0",
    "org.mongodb:mongo-java-driver:3.12.10",
    "org.mongodb:mongodb-driver-core:4.3.1",
    "org.mongodb:mongodb-driver:3.12.10",
    "org.nd4j:nd4j-api:1.0.0-beta7",
    "org.ojalgo:ojalgo:48.3.0",
    "org.openapitools.swagger.parser:swagger-parser-v3:2.0.14-OpenAPITools.org-1",
    "org.openapitools.swagger.parser:swagger-parser:2.0.14-OpenAPITools.org-1",
    "org.openapitools:jackson-databind-nullable:0.2.1",
    "org.openapitools:openapi-generator-cli:5.2.0",
    "org.openapitools:openapi-generator-core:5.2.0",
    "org.openapitools:openapi-generator-maven-plugin:5.2.0",
    "org.openapitools:openapi-generator:5.2.0",
    "org.openjfx:javafx-base:17-ea+7",
    "org.openjfx:javafx-controls:17-ea+7",
    "org.openjfx:javafx-fxml:17-ea+7",
    "org.openjfx:javafx-graphics:17-ea+7",
    "org.openjfx:javafx-media:17-ea+7",
    "org.openjfx:javafx-swing:17-ea+7",
    "org.openjfx:javafx-web:17-ea+7",
    "org.openjfx:javafx:17-ea+7",
    "org.optaplanner:optaplanner-core:8.4.1.Final",
    "org.optaplanner:optaplanner-persistence-jackson:8.4.1.Final",
    "org.postgresql:postgresql:42.2.23",
    "org.projectlombok:lombok:1.18.20",
    "org.reactivestreams:reactive-streams:1.0.3",
    "org.redisson:redisson:3.16.1",
    "org.reflections:reflections:0.9.12",
    "org.scala-lang:scala-library:2.13.4",
    "org.scala-lang:scala-reflect:2.13.3",
    "org.slf4j:jul-to-slf4j:1.7.30",
    "org.slf4j:slf4j-api:1.7.30",
    "org.slf4j:slf4j-jdk14:1.7.30",
    "org.springframework.boot:spring-boot-autoconfigure:2.5.3",
    "org.springframework.boot:spring-boot-starter-actuator:2.5.3",
    "org.springframework.boot:spring-boot-starter-jdbc:2.5.3",
    "org.springframework.boot:spring-boot-starter-jetty:2.5.3",
    "org.springframework.boot:spring-boot-starter-log4j2:2.5.3",
    "org.springframework.boot:spring-boot-starter-web:2.5.3",
    "org.springframework.boot:spring-boot-starter-webflux:2.5.3",
    "org.springframework.boot:spring-boot-starter:2.5.3",
    "org.springframework.boot:spring-boot:2.5.3",
    "org.springframework.kafka:spring-kafka-test:2.7.5",
    "org.springframework.kafka:spring-kafka:2.7.5",
    "org.springframework.security:spring-security-core:5.4.1",
    "org.springframework:spring-aop:5.3.5",
    "org.springframework:spring-beans:5.3.5",
    "org.springframework:spring-context:5.3.5",
    "org.springframework:spring-core:5.3.5",
    "org.springframework:spring-expression:5.3.5",
    "org.springframework:spring-jdbc:5.3.9",
    "org.springframework:spring-test:5.3.5",
    "org.springframework:spring-web:5.3.5",
    "org.testcontainers:cassandra:1.16.0",
    "org.testcontainers:cockroachdb:1.16.0",
    "org.testcontainers:couchbase:1.16.0",
    "org.testcontainers:database-commons:1.16.0",
    "org.testcontainers:db2:1.16.0",
    "org.testcontainers:elasticsearch:1.16.0",
    "org.testcontainers:gcloud:1.16.0",
    "org.testcontainers:jdbc-test:1.12.0",
    "org.testcontainers:jdbc:1.16.0",
    "org.testcontainers:junit-jupiter:1.16.0",
    "org.testcontainers:kafka:1.16.0",
    "org.testcontainers:mariadb:1.16.0",
    "org.testcontainers:mockserver:1.16.0",
    "org.testcontainers:mongodb:1.16.0",
    "org.testcontainers:mysql:1.16.0",
    "org.testcontainers:mysql:1.16.0",
    "org.testcontainers:oracle-xe:1.16.0",
    "org.testcontainers:postgresql:1.16.0",
    "org.testcontainers:r2dbc:1.16.0",
    "org.testcontainers:testcontainers:1.15.3",
    "org.threeten:threetenbp:1.4.5",
    "org.xerial:sqlite-jdbc:3.34.0",
    "org.yaml:snakeyaml:1.28",
    "oro:oro:2.0.8",
    "redis.clients:jedis:3.6.3",
    "xerces:xercesImpl:2.12.0",
    "xml-apis:xml-apis:1.4.01"
  )
}
