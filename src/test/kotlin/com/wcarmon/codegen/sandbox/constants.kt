package com.wcarmon.codegen.sandbox

object TemplatePaths {
  // -- Gradle
  const val GRADLE_BUILD_FILE = "/sandbox/templates/gradle/build.gradle.kts.ftl"
  const val GRADLE_PROPERTIES_FILE = "/sandbox/templates/gradle/gradle.properties.ftl"
  const val GRADLE_PROTO_FILE = "/sandbox/templates/gradle/protobuf.gradle"
  const val GRADLE_SETTINGS_FILE = "/sandbox/templates/gradle/settings.gradle.kts.ftl"
  const val JUNIT_PROPS_FILE = "/sandbox/templates/gradle/junit-platform.properties.ftl"
  const val LOG4J_CONFIG_FILE = "/sandbox/templates/gradle/log4j2.xml.ftl"
  const val LOG4J_TEST_CONFIG_FILE = "/sandbox/templates/gradle/log4j2-test.xml.ftl"
  const val SANDBOX_MAIN_KT_FILE = "/sandbox/templates/gradle/SandboxMain.kt.ftl"
  const val MANIFEST_MF_FILE = "/sandbox/templates/gradle/MANIFEST.MF.ftl"
  const val SPRING_APP_PROPS_FILE = "/sandbox/templates/gradle/application.properties.ftl"

  // -- NPM
  const val PACKAGE_JSON_FILE = "/sandbox/templates/npm/package.json.ftl"
  //TODO: .browserslistrc
  //TODO: .editorconfig
  //TODO: .eslintignore
  //TODO: .eslintrc.json
  //TODO: .prettierignore
  //TODO: .prettierrc.js
  //TODO: angular.json
  //TODO: karma.conf.js
  //TODO: package.json
  //TODO: tsconfig.app.json
  //TODO: tsconfig.json
  //TODO: tsconfig.spec.json
  //TODO: tsconfig.worker.json
}

val TEMPLATE_TO_RELATIVE_OUTPUT_PATH_MAPPING = mapOf(
  TemplatePaths.GRADLE_BUILD_FILE to "build.gradle.kts",
  TemplatePaths.GRADLE_PROPERTIES_FILE to "gradle.properties",
  TemplatePaths.GRADLE_SETTINGS_FILE to "settings.gradle.kts",
  TemplatePaths.JUNIT_PROPS_FILE to "src/test/resources/junit-platform.properties",
  TemplatePaths.LOG4J_CONFIG_FILE to "src/main/resources/log4j2.xml",
  TemplatePaths.LOG4J_TEST_CONFIG_FILE to "src/test/resources/log4j2-test.xml",
  TemplatePaths.SANDBOX_MAIN_KT_FILE to "src/main/kotlin/SandboxMain.kt",
  TemplatePaths.MANIFEST_MF_FILE to "src/main/resources/META-INF/MANIFEST.MF",
  TemplatePaths.SPRING_APP_PROPS_FILE to "src/main/resources/application.properties",
)

val RELATIVE_DIRS_FOR_GRADLE_PROJECT = listOf(
  "src/gen/java",
  "src/gen/kotlin",
  "src/gen/proto",
  "src/main/java",
  "src/main/kotlin",
  "src/main/proto",
  "src/main/resources",
  "src/main/resources/META-INF",
  "src/main/sqldelight",
  "src/test/java",
  "src/test/kotlin",
  "src/test/resources",
)

val RELATIVE_DIRS_FOR_NPM_PROJECT = listOf(
  "src",
  "scripts",
)
