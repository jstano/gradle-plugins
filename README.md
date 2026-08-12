# gradle-plugins

A suite of Gradle plugins for building opinionated Spring Boot applications that follow clean architecture principles. These plugins are the build-system companion to the [Modular Spring Platform (MSP)](https://github.com/jstano/modular-spring-platform) — they wire up the compiler, formatter, test runner, JaCoCo coverage, Maven Central publishing, Docker build, and SonarQube integration so teams can focus on domain logic rather than Gradle configuration.

## About the Modular Spring Platform

These plugins are designed for use with the [Modular Spring Platform (MSP)](https://github.com/jstano/modular-spring-platform), an opinionated framework for building modular Spring Boot applications structured around clean architecture layers (domain, application, infrastructure, web). The plugins handle the build plumbing — BOM imports, annotation processors, OTel agent packaging, Docker image naming conventions — leaving the MSP libraries to enforce architectural boundaries at the code level.

## Overview

This project publishes 13 Gradle plugins to the Gradle Plugin Portal. The plugins are designed to work together as a cohesive build system:

- **Settings-level**: `com.stano.settings` (configures repositories, build cache, plugin versions)
- **Root project**: `com.stano.base`, `com.stano.application`, `com.stano.library` (set up base infrastructure and versioning)
- **Subproject**: `com.stano.java`, `com.stano.java-library`, `com.stano.spring-boot`, `com.stano.maven-central-publish` (configure compilers, testing, publishing)
- **Optional infrastructure**: `com.stano.sonar` (SonarQube), `com.stano.kotlin` (Kotlin JVM support), `com.stano.docker*` (Docker build/run)

**Plugin dependency hierarchy:**

```
com.stano.settings (settings.gradle.kts)
    ↓
com.stano.base ← root project prerequisite
    ↓
com.stano.application OR com.stano.library
    ↓
(on subprojects)
com.stano.java ← required for Java subprojects
    ├── com.stano.java-library (extends java, adds private-repo publishing)
    │       └── com.stano.maven-central-publish (composable alongside java-library)
    └── com.stano.spring-boot (alongside java for Spring apps)

Optional (any project):
    com.stano.sonar
    com.stano.kotlin (extend java for Kotlin compilation)
    com.stano.docker
    com.stano.docker-compose
    com.stano.docker-run
```

## Getting Started

### 1. Enable `com.stano.settings` in `settings.gradle.kts`

This **must** be the first plugin block, before `rootProject.name`:

```kotlin
plugins {
  id("com.stano.settings") version "0.1.12" // check the latest published version
}

rootProject.name = "my-app"

// Configure the build cache prefix (optional; defaults to rootProject.name)
buildCacheSettings {
  buildCachePrefix.set("my-custom-prefix")
}

// Configure private Maven repository URL (optional)
// Can also be set via gradle.properties or env var STANO_MAVEN_URL
```

This plugin also pins the version of every other `com.stano.*` plugin (and the Kotlin JVM plugin), so subprojects don't need to specify a `version(...)` on their own `plugins { id(...) }` blocks. See [`docs/settings.md`](docs/settings.md) for details.

### 2. Apply `com.stano.application` or `com.stano.library` to the Root Project

**For applications** (auto-versioned from git):

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.application")
}
```

**For libraries** (no auto-versioning):

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.library")
}
```

### 3. Apply `com.stano.java` (or `com.stano.java-library`) to Subprojects

```kotlin
// build.gradle.kts (each Java/Kotlin subproject)
plugins {
  id("com.stano.java")
}

// For a library that publishes to Maven:
// id("com.stano.java-library")
```

---

## Plugin Reference

Full documentation for each plugin — extension properties, tasks, gotchas, and worked examples — lives under [`docs/`](docs):

| Plugin ID | Docs |
|---|---|
| `com.stano.settings` | [`docs/settings.md`](docs/settings.md) |
| `com.stano.base` | [`docs/base.md`](docs/base.md) |
| `com.stano.application` | [`docs/application.md`](docs/application.md) |
| `com.stano.library` | [`docs/library.md`](docs/library.md) |
| `com.stano.java` | [`docs/java.md`](docs/java.md) |
| `com.stano.java-library` | [`docs/java-library.md`](docs/java-library.md) |
| `com.stano.maven-central-publish` | [`docs/maven-central-publish.md`](docs/maven-central-publish.md) |
| `com.stano.kotlin` | [`docs/kotlin.md`](docs/kotlin.md) |
| `com.stano.spring-boot` | [`docs/spring-boot.md`](docs/spring-boot.md) |
| `com.stano.sonar` | [`docs/sonar.md`](docs/sonar.md) |
| `com.stano.docker` | [`docs/docker.md`](docs/docker.md) |
| `com.stano.docker-compose` | [`docs/docker-compose.md`](docs/docker-compose.md) |
| `com.stano.docker-run` | [`docs/docker-run.md`](docs/docker-run.md) |
| `gradle-plugins-bom` (not a plugin) | [`docs/bom.md`](docs/bom.md) |

---

## Typical Project Layouts

### Layout 1: Spring Boot Application

**Project structure:**

```
my-app/
├── settings.gradle.kts
├── build.gradle.kts (root)
├── app/
│   ├── build.gradle.kts
│   ├── src/main/java/...
│   └── src/main/resources/application.yml
├── lib-common/
│   └── build.gradle.kts
└── Dockerfile
```

**`settings.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.settings") version "0.1.12"
}

rootProject.name = "my-app"
buildCacheSettings { buildCachePrefix.set("my-org") }

include("app", "lib-common")
```

**Root `build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.application")
  id("com.stano.sonar")
}

root {
  javaVersion = "21"
  mspVersion = "2.0.0"
  contextName = "my-app"
  dockerRegistryHost = "docker.mycompany.com"
}
```

**`app/build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.java")
  id("com.stano.spring-boot")
  id("com.stano.docker")
}

dependencies {
  implementation(project(":lib-common"))
  implementation("org.springframework.boot:spring-boot-starter-web")
}

docker {
  buildArgs(mapOf("BASE_IMAGE" to "eclipse-temurin:21-jdk-alpine"))
}
```

**`lib-common/build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.java-library")
}

dependencies {
  api("com.fasterxml.jackson.core:jackson-databind")
}
```

**Build & run:**

```bash
# Build, test, and publish libraries
./gradlew build

# Build Docker image
./gradlew :app:docker

# Run tests with SonarQube
./gradlew test sonarqube

# Generate coverage report
./gradlew jacocoRootReport
```

### Layout 2: Multi-Module Library

**Project structure:**

```
my-lib-suite/
├── settings.gradle.kts
├── build.gradle.kts (root)
├── gradle.properties  # version = 1.2.3
├── core/
│   └── build.gradle.kts
├── spring-integration/
│   └── build.gradle.kts
└── testing-utils/
    └── build.gradle.kts
```

**`settings.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.settings") version "0.1.12"
}

rootProject.name = "my-lib-suite"
include("core", "spring-integration", "testing-utils")
```

**Root `build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.library")
}

version = "1.2.3"  // managed in gradle.properties or here
```

**`core/build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.java-library")
}

dependencies {
  api("com.google.guava:guava:33.0.0-jre")
}
```

**`spring-integration/build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.java-library")
}

dependencies {
  api(project(":core"))
  implementation("org.springframework:spring-core:6.0.0")
}
```

**Build & publish:**

```bash
# Build and test all modules
./gradlew build

# Publish all JARs + sources + javadoc to Maven
./gradlew publish

# Generate coverage report
./gradlew jacocoRootReport
```

For publishing to Maven Central instead of (or alongside) a private repository, add `com.stano.maven-central-publish` to a module — see [`docs/maven-central-publish.md`](docs/maven-central-publish.md).

---

## Maintenance

### Upgrading the Gradle Wrapper

```bash
./gradlew wrapper --gradle-version <version>
```

### Useful Links

* [Gradle S3 Build Cache Plugin](https://github.com/burrunan/gradle-s3-build-cache)
* [Modular Spring Platform (MSP)](https://github.com/jstano/modular-spring-platform)
