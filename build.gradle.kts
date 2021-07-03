plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.5.0"

  id("org.jetbrains.kotlin.plugin.allopen") version "1.5.0"
  id("org.jetbrains.kotlin.plugin.spring") version "1.5.0"

  id("org.springframework.boot") version "2.4.5"

  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("io.gitlab.arturbosch.detekt") version "1.16.0"
  id("com.github.ben-manes.versions") version "0.38.0"
  id("com.diffplug.spotless") version "5.12.4"
}

apply(plugin = "java")
apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "com.diffplug.spotless")
apply(plugin = "io.gitlab.arturbosch.detekt")

group = "com.wcarmon"
version = "0.1.0-SNAPSHOT"

configurations.all {
  resolutionStrategy.failOnVersionConflict()

  exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")

  resolutionStrategy.force(
    //TODO: more here
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
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.lmax:disruptor")
  implementation("org.apache.commons:commons-compress")
  implementation("org.apache.commons:commons-lang3")
  implementation("org.apache.commons:commons-text")
  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.velocity.tools:velocity-tools-generic")
  implementation("org.apache.velocity:velocity-engine-core")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-log4j2")
  implementation("org.yaml:snakeyaml")
  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-jdk8"))
}

sourceSets {

  main {
    java.srcDirs(
      "src/main/java",
      "src/main/kotlin"
    )
  }

  test {
    java.srcDirs(
      "src/main/java",
      "src/main/kotlin",
      "src/test/java",
      "src/test/kotlin"
    )
  }
}

tasks {

  jar {
    manifest {
// TODO: set main class
//      attributes("Main-Class" to mainClass)
    }
  }
}
