# Contributing to gradle-plugins

Thanks for contributing! This guide walks you through setup, conventions, and the process for adding new plugins.

## Prerequisites

- **JDK 21** — required by the Java toolchain configuration
- **Gradle** — use the bundled wrapper (`./gradlew`); no local installation needed
- **Access to Stano Maven repository** — credentials required for dependency resolution

## Development Environment Setup

### 1. Configure Maven Repository Access

The project requires credentials for the private Stano Maven repository to resolve dependencies.

**Option A: `~/.gradle/gradle.properties` (recommended)**

Add to your home Gradle properties file:

```properties
com.stano.maven.url=https://maven.stano.com/repository/releases
com.stano.maven.username=<username>
com.stano.maven.password=<password>
```

**Option B: Environment Variables**

Alternatively, export:

```bash
export STANO_MAVEN_URL=https://maven.stano.com/repository/releases
export STANO_MAVEN_USERNAME=<username>
export STANO_MAVEN_PASSWORD=<password>
```

### 2. Optional: S3 Build Cache (for faster CI/local builds)

If your team uses S3 for build caching:

```properties
com.stano.build-cache.s3.bucket=<bucket-name>
com.stano.build-cache.s3.region=us-east-1
com.stano.build-cache.s3.access-key-id=<key>
com.stano.build-cache.s3.secret-access-key=<secret>
com.stano.build-cache.push-enabled=false  # set to true if you have write access
```

### 3. Optional: SonarQube (for local analysis)

```properties
com.stano.sonar.host=https://sonarqube.stano.com
com.stano.sonar.token=<token>
```

## Build & Test

All commands use the Gradle wrapper — no need to install Gradle locally.

```bash
# Build all submodules and run tests
./gradlew build

# Run all tests only
./gradlew test

# Build or test a single submodule
./gradlew :gradle-plugins-sonar:build
./gradlew :gradle-plugins-docker:test

# Auto-fix code formatting (run before every commit!)
./gradlew spotlessApply

# Verify formatting (what CI runs)
./gradlew spotlessCheck

# Generate JaCoCo coverage report (HTML at build/reports/jacoco/test/html/index.html)
./gradlew jacocoRootReport

# Run SonarQube analysis (requires sonar.host and sonar.token)
./gradlew sonarqube
```

## Project Layout

```
gradle-plugins/
├── gradle-plugins-base/          # Shared plugin infra (BaseExtension, PluginFeature interface)
├── gradle-plugins-application/   # com.stano.application plugin
├── gradle-plugins-library/       # com.stano.library plugin
├── gradle-plugins-java/          # com.stano.java plugin (core Java setup)
├── gradle-plugins-java-library/  # com.stano.java-library plugin (adds publishing)
├── gradle-plugins-spring-boot/   # com.stano.spring-boot plugin
├── gradle-plugins-settings/      # com.stano.settings plugin (applied in settings.gradle.kts)
├── gradle-plugins-sonar/         # com.stano.sonar plugin (SonarQube integration)
├── gradle-plugins-docker/        # com.stano.docker, docker-compose, docker-run plugins
├── gradle-plugins-bom/           # BOM that aggregates all plugin JARs as constraints
├── gradle/libs.versions.toml     # Centralized dependency versions (NEVER hardcode versions)
├── AGENTS.md                     # Technical rules for AI agents (authoritative source)
├── CONTRIBUTING.md               # This file
└── README.md                     # Consumer documentation for all plugins
```

Each `gradle-plugins-*` submodule is independently published as a Gradle plugin to both the Gradle Plugin Portal (public) and the Stano Maven repository (private).

## Architecture: The PluginFeature Pattern

Every plugin in this suite follows the same decomposition pattern:

1. **Plugin class** (`<Name>Plugin implements Plugin<Project>`)
   - Minimal `apply()` method
   - Just instantiates features and calls their `apply(project)` method

2. **Feature classes** (each in `com.stano.gradle.<domain>.features`)
   - Each class implements `PluginFeature` with a single `void apply(Project project)` method
   - One concern per class (e.g., `ConfigureCompilersFeature`, `ConfigureSpotlessFeature`)
   - No dependency injection — features are simple POJOs

**Example plugin structure:**

```java
// com/stano/gradle/myfeature/MyPlugin.java
public class MyPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new MyExtensionFeature().apply(project);
    new MyBuildArgsFeature().apply(project);
  }
}

// com/stano/gradle/myfeature/features/MyExtensionFeature.java
public class MyExtensionFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    project.getExtensions().create("myConfig", MyExtension.class);
  }
}
```

## Adding a New Plugin

### Step 1: Create the Submodule

```bash
mkdir gradle-plugins-myfeature
cd gradle-plugins-myfeature
mkdir -p src/main/java/com/stano/gradle/myfeature/features
mkdir -p src/test/java/com/stano/gradle/myfeature
mkdir -p src/main/resources/META-INF/gradle-plugins
```

### Step 2: Add to `settings.gradle.kts`

```kotlin
include("gradle-plugins-myfeature")
```

### Step 3: Create `build.gradle.kts`

Copy the pattern from an existing plugin (e.g., `gradle-plugins-sonar/build.gradle.kts`):

```kotlin
plugins {
  id("java-library")
  id("maven-publish")
  id("jacoco")
  id("com.diffplug.spotless")
}

dependencies {
  implementation(project(":gradle-plugins-base"))
  implementation("org.gradle:gradle-api:8.8")

  testFixtures(project(":gradle-plugins-base"))
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.assertj.core)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

publishing {
  publications {
    maven(MavenPublication) {
      from(components.java)
    }
  }
}
```

### Step 4: Create the Plugin Class

**`src/main/java/com/stano/gradle/myfeature/MyPlugin.java`**

```java
package com.stano.gradle.myfeature;

import com.stano.gradle.myfeature.features.ConfigureMyFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MyPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new ConfigureMyFeature().apply(project);
  }
}
```

### Step 5: Create Feature Classes

**`src/main/java/com/stano/gradle/myfeature/features/ConfigureMyFeature.java`**

```java
package com.stano.gradle.myfeature.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;

public class ConfigureMyFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    // Your plugin logic here
  }
}
```

### Step 6: Register the Plugin ID

Create `src/main/resources/META-INF/gradle-plugins/com.stano.myfeature.properties`:

```properties
implementation-class=com.stano.gradle.myfeature.MyPlugin
```

### Step 7: Write Tests

**`src/test/java/com/stano/gradle/myfeature/MyPluginTest.java`**

```java
package com.stano.gradle.myfeature;

import com.stano.gradle.base.BasePluginTest;
import org.gradle.api.Project;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MyPluginTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheMyConfigExtension() {
    Project project = childProject;
    project.getPlugins().apply(MyPlugin.class);

    assertThat(project.getExtensions().findByName("myConfig")).isNotNull();
  }
}
```

**Test conventions:**
- Extend `BasePluginTest` from `testFixtures(project(":gradle-plugins-base"))`
  - Automatically provides `rootProject` and `childProject` via `ProjectBuilder`
- Use AssertJ for assertions: `assertThat(...).isEqualTo(...)`
- Use long descriptive method names: `applyingThePluginShouldDoX`
- Annotate slow/network-dependent tests with `@Disabled`
- Always run tests via `./gradlew test`, not IDE runner

### Step 8: Update the BOM

Add your plugin JAR as a constraint in `gradle-plugins-bom/build.gradle.kts`:

```kotlin
dependencies {
  constraints {
    implementation(project(":gradle-plugins-myfeature"))
  }
}
```

### Step 9: Pin Plugin Version

The `SettingsPlugin` automatically reads the plugin version from `stano-plugins.properties` during the build. After your first build, the version will be pinned automatically. No manual action needed.

## Dependency Management

**All library versions must be in `gradle/libs.versions.toml`** — never hardcode version numbers in `build.gradle.kts`.

### Adding a New Dependency

1. Add a version alias under `[versions]`:

```toml
[versions]
my-lib = "1.2.3"
```

2. Add a library alias under `[libraries]`:

```toml
[libraries]
my-lib = { group = "com.example", name = "my-lib", version.ref = "my-lib" }
```

3. Reference it in your `build.gradle.kts`:

```kotlin
dependencies {
  implementation(libs.myLib)
}
```

### Global Exclusions

`commons-logging` and `log4j` are globally excluded from all configurations in projects applying `com.stano.java`. Do not add them back.

## Coding Conventions

### Language

- **Plugin implementations**: Java 21 only — no Kotlin source files in plugin modules
- **Build scripts**: Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`)

### Package Naming

All plugin code lives under `com.stano.gradle`:

```
com.stano.gradle.base                    (gradle-plugins-base)
com.stano.gradle.base.changecase         (case conversion utilities)
com.stano.gradle.application             (gradle-plugins-application)
com.stano.gradle.java                    (gradle-plugins-java)
com.stano.gradle.java.features           (feature classes for com.stano.java)
com.stano.gradle.javalibrary             (gradle-plugins-java-library)
com.stano.gradle.library                 (gradle-plugins-library)
com.stano.gradle.springboot              (gradle-plugins-spring-boot)
com.stano.gradle.sonar                   (gradle-plugins-sonar)
com.stano.gradle.docker                  (gradle-plugins-docker)
com.stano.gradle.settings                (gradle-plugins-settings)
```

Feature classes always live in a `.features` subpackage.

### Code Style

Enforced by Spotless (see `.editorconfig` and `build.gradle.kts`):

- **Indentation**: 2 spaces (no tabs)
- **Line endings**: LF (Unix style)
- **Encoding**: UTF-8
- **Imports**: 
  - No wildcard imports (`import java.util.*;` ✗)
  - No unused imports
- **Trailing whitespace**: None
- **Final newline**: Required

**Before every commit**, run:

```bash
./gradlew spotlessApply
```

CI will reject commits that fail `spotlessCheck`.

## Publishing

Plugins are published to two repositories automatically in CI:

### Gradle Plugin Portal (Public)

Manual publishing (usually done in CI):

```bash
./gradlew publishPlugins
```

**One-time setup:** Obtain API keys from https://plugins.gradle.org after logging in, then add to `~/.gradle/gradle.properties`:

```properties
gradle.publish.key=<key>
gradle.publish.secret=<secret>
```

### Stano Maven Repository (Private)

Manual publishing:

```bash
./gradlew publish
```

Credentials required (from dev environment setup above).

## Important Constraints

- **Do not edit** `build/` or `src/generated/` — these are overwritten by Gradle tasks
- **Do not uncomment** inactive submodules in `settings.gradle.kts` without explicit reason
- **Do not add** `gradle-plugins-docker` to `gradle-plugins-bom` — it is intentionally excluded
- **Always run** `./gradlew spotlessApply` before committing
- **Always run** tests locally: `./gradlew test`

## Troubleshooting

### Maven Repository Not Found

**Error:** "Could not find artifact com.example:lib:1.0.0"

**Fix:** Verify `STANO_MAVEN_URL`, `STANO_MAVEN_USERNAME`, `STANO_MAVEN_PASSWORD` are set in `~/.gradle/gradle.properties` or as environment variables.

### Spotless Formatting Failures

**Error:** Tests fail with formatting errors.

**Fix:** Run `./gradlew spotlessApply` to auto-fix, then re-run tests.

### JDK Version Mismatch

**Error:** "Source option 21 is not supported by this compiler"

**Fix:** Ensure JDK 21 is in your `$JAVA_HOME`:

```bash
java -version  # should show version 21.x
```

## Need Help?

- **Plugin architecture questions?** See `AGENTS.md` (authoritative technical reference)
- **User-facing plugin docs?** See `README.md` (consumer documentation with examples)
- **Code questions?** Read the source of a similar plugin (e.g., `gradle-plugins-sonar` for a simple plugin)
