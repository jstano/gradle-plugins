# gradle-plugins Agent Guidance

## Build and Test

```bash
./gradlew build          # Build all subprojects
./gradlew test           # Run all tests with JUnit Platform
./gradlew sonarqube      # Run SonarQube analysis
./gradlew :MODULE:build  # Build specific subproject (e.g., :gradle-plugins-test:build)
```

## Environment Setup

Optional environment variables (can also be set via gradle.properties or extraProperties):
- `STANO_SONAR_HOST_URL` — SonarQube host
- `STANO_SONAR_TOKEN` — SonarQube auth token
- `STANO_MAVEN_URL` — Private Maven repository URL
- `STANO_MAVEN_USERNAME`, `STANO_MAVEN_PASSWORD` — Maven credentials
- `STANO_BUILD_CACHE_S3_BUCKET`, `STANO_BUILD_CACHE_S3_REGION`, `STANO_BUILD_CACHE_S3_ACCESS_KEY_ID`, `STANO_BUILD_CACHE_S3_SECRET_ACCESS_KEY` — S3 build cache

## Dependency Management

All dependencies are centralized in `buildSrc/src/main/kotlin/com/stano/buildlogic/Dependencies.kt`. When adding a new dependency:

1. Add the full coordinate string to the `dependencyList`
2. Reference it via `fullDependency("org.example:artifact")` in build files

**Never hardcode dependency versions in build.gradle.kts files** — use the centralized `fullDependency()` function. Why: version consistency across the monorepo. How: check Dependencies.kt before writing any dependency line.

## Target Versions

- Java: 21
- Kotlin: 2.4.0
- Gradle: 8.14.5
- Groovy: 3.0.25

## Critical Constraints

**Do not edit generated files.** Build outputs in `build/`, generated source in `src/generated/` are overwritten by Gradle tasks. Make changes to source files only.

**Inactive submodules in settings.gradle.kts are intentionally disabled.** They are commented out to prevent accidental inclusion. Do not uncomment them without explicit user request.

**Test Execution:** All tests use JUnit Platform and must be run via `./gradlew test`, never by IDE test runners directly. JaCoCo reports are auto-generated and required.

## Context

- This is a monorepo of Gradle plugins (not an application)
- Each `gradle-plugins-*` submodule is an independently published plugin
- buildSrc contains shared build logic and dependency definitions for all submodules
