plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm")

  id("maven-publish")

  id("org.jetbrains.kotlin.plugin.allopen")
  id("org.jetbrains.kotlin.plugin.spring")

  id("org.springframework.boot")

  id("com.github.johnrengelman.shadow")
  id("io.gitlab.arturbosch.detekt")
  id("com.github.ben-manes.versions")
  id("com.diffplug.spotless")
}

apply(plugin = "java")
apply(plugin = "org.jetbrains.kotlin.jvm")

apply(plugin = "com.diffplug.spotless")
apply(plugin = "io.gitlab.arturbosch.detekt")

group = "com.wcarmon"
version = "0.1.0-SNAPSHOT"

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

  // MAINTENANCE: diff with latest from ~/git-repos/modern-jvm/gradle-parent/build.gradle.kts
  resolutionStrategy.force(
    "ch.qos.logback.contrib:logback-jackson:0.1.5",
    "ch.qos.logback:logback-access:1.2.3",
    "ch.qos.logback:logback-classic:1.2.3",
    "ch.qos.logback:logback-core:1.2.3",
    "com.auth0:java-jwt:3.11.0",
    "com.fasterxml.jackson.core:jackson-annotations:2.12.4",
    "com.fasterxml.jackson.core:jackson-core:2.12.4",
    "com.fasterxml.jackson.core:jackson-databind:2.12.4",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.12.4",
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
    "com.fasterxml.jackson:jackson-bom:2.12.4",
    "com.github.ben-manes.caffeine:caffeine:2.8.5",
    "com.github.javafaker:javafaker:1.0.2",
    "com.google.guava:guava:30.0-jre",
    "com.h2database:h2:1.4.200",
    "com.lmax:disruptor:3.4.4",
    "commons-beanutils:commons-beanutils:1.6.1",
    "commons-codec:commons-codec:1.11",
    "commons-collections:commons-collections:3.2.1",
    "commons-io:commons-io:2.2",
    "commons-lang:commons-lang:2.6",
    "io.netty:netty-buffer:4.1.65.Final",
    "io.netty:netty-common:4.1.65.Final",
    "io.netty:netty-resolver:4.1.65.Final",
    "io.netty:netty-transport-native-epoll:4.1.65.Final",
    "io.netty:netty-transport-native-unix-common:4.1.65.Final",
    "io.netty:netty-transport:4.1.65.Final",
    "junit:junit:4.13.1",
    "net.java.dev.jna:jna:5.8.0",
    "org.apache.commons:commons-compress:1.21",
    "org.apache.commons:commons-lang3:3.12.0",
    "org.apache.commons:commons-math3:3.6.1",
    "org.apache.commons:commons-text:1.9",
    "org.apache.velocity.tools:velocity-tools-generic:3.1",
    "org.apache.velocity:velocity-engine-core:2.3",
    "org.atteo:evo-inflector:1.3",
    "org.awaitility:awaitility-kotlin:4.0.3",
    "org.awaitility:awaitility:4.0.3",
    "org.jetbrains.kotlin:kotlin-compiler-embeddable:1.5.30-M1",
    "org.jetbrains.kotlin:kotlin-reflect:1.5.30-M1",
    "org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.5.30-M1",
    "org.jetbrains.kotlin:kotlin-stdlib-common:1.5.30-M1",
    "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.30-M1",
    "org.jetbrains.kotlin:kotlin-stdlib:1.5.30-M1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.1",
    "org.jetbrains:annotations:20.1.0",
    "org.junit.jupiter:junit-jupiter-api:5.7.0",
    "org.junit.jupiter:junit-jupiter-engine:5.7.0",
    "org.junit.jupiter:junit-jupiter-params:5.7.0",
    "org.junit.platform:junit-platform-launcher:1.7.0",
    "org.junit.vintage:junit-vintage-engine:5.7.0",
    "org.mockito:mockito-inline:3.6.0",
    "org.projectlombok:lombok:1.18.20",
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
    "org.springframework.security:spring-security-core:5.4.1",
    "org.springframework:spring-aop:5.3.5",
    "org.springframework:spring-beans:5.3.5",
    "org.springframework:spring-context:5.3.5",
    "org.springframework:spring-core:5.3.5",
    "org.springframework:spring-expression:5.3.5",
    "org.springframework:spring-jdbc:5.3.9",
    "org.springframework:spring-test:5.3.5",
    "org.springframework:spring-web:5.3.5",
    "org.testcontainers:junit-jupiter:1.15.3",
    "org.testcontainers:testcontainers:1.15.3",
    "org.yaml:snakeyaml:1.28",
    "oro:oro:2.0.8",
    "xerces:xercesImpl:2.12.0",
    "xml-apis:xml-apis:1.4.01"
  )
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
  implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.google.guava:guava")
  implementation("com.lmax:disruptor")
  implementation("org.apache.commons:commons-compress")
  implementation("org.apache.commons:commons-lang3")
  implementation("org.apache.commons:commons-text")
  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.velocity.tools:velocity-tools-generic")
  implementation("org.apache.velocity:velocity-engine-core")
  implementation("org.atteo:evo-inflector")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-log4j2")
  implementation("org.yaml:snakeyaml")
  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-jdk8"))

  testImplementation("org.apache.commons:commons-compress")
  testImplementation("org.apache.commons:commons-lang3")
  testImplementation("org.apache.commons:commons-text")
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.junit.jupiter:junit-jupiter-engine")
  testImplementation("org.junit.jupiter:junit-jupiter-params")
  testImplementation("org.mockito:mockito-inline")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:testcontainers")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {

  main {
    java.srcDirs(
      "src/gen/java",
      "src/gen/kotlin",
      "src/main/java",
      "src/main/kotlin"
    )
  }

  test {
    java.srcDirs(
      "src/gen/java",
      "src/gen/kotlin",
      "src/main/java",
      "src/main/kotlin",
      "src/test/java",
      "src/test/kotlin"
    )
  }
}


tasks.jar {
  manifest {
// TODO: set main class
//      attributes("Main-Class" to mainClass)
  }
}

tasks.withType<JavaCompile>().configureEach {
//    options.forkOptions.javaHome = file(java11Home)
  options.isDebug = false
  options.isFailOnError = true
  options.isFork = true
  options.isIncremental = true
  options.isVerbose = false
//    options.release.set(11)
}

// https://github.com/JetBrains/kotlin/blob/master/libraries/tools/kotlin-gradle-plugin/src/main/kotlin/org/jetbrains/kotlin/gradle/dsl/KotlinCompile.kt
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
//      jdkHome = java11Home
    jvmTarget = "11"
  }
}

tasks.withType<Test> {

  failFast = true
  useJUnitPlatform { }

  testLogging {
    events("passed", "skipped", "failed", "standardOut", "standardError")
    showExceptions = true
    showStandardStreams = true
  }

}

publishing {

  repositories {

    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/wcarmon/codegen")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }
  }

  publications {
    register<MavenPublication>("gpr") {
      from(components["java"])
    }
  }
}
