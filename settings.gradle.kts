pluginManagement {
  plugins {
    //TODO: bump versions
    id("com.github.ben-manes.versions") version "0.33.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("com.gradle.build-scan") version "3.4.1"
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.4.10"
    id("org.springframework.boot") version "2.3.4.RELEASE" // bootRun
  }

  repositories {
    mavenLocal()
    mavenCentral()

    google()
    gradlePluginPortal()
  }
}

rootProject.name = "codegen"
