# Overview


# Output
- SQLDelight writes to `build/generated/sqldelight/code/<DatabaseNameInBuildDotGradle>/com/foo/FooRecord.kt`


# Gradle
## Generate
```bash
./gradlew :sandbox:generateMain<DatabaseName>Interface;
./gradlew :sandbox:generateMainSandboxDatabaseInterface;
./gradlew :sandbox build -x test;
```

# Gotcha
- Intellij build doesn't generate from *.sq to *.kt (have to run gradle)
