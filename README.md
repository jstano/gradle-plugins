# gradle-plugins

A suite of Gradle plugins for building Java/Kotlin applications and libraries with sensible, production-ready defaults. Plugins handle compiler configuration, test execution, code formatting, JaCoCo coverage aggregation, Docker builds, SonarQube integration, and more.

## Overview

This project publishes 11 Gradle plugins to the Gradle Plugin Portal. The plugins are designed to work together as a cohesive build system:

- **Settings-level**: `com.stano.settings` (configures repositories, build cache, plugin versions)
- **Root project**: `com.stano.base`, `com.stano.application`, `com.stano.library` (set up base infrastructure and versioning)
- **Subproject**: `com.stano.java`, `com.stano.java-library`, `com.stano.spring-boot` (configure compilers, testing, publishing)
- **Optional infrastructure**: `com.stano.sonar` (SonarQube), `com.stano.docker*` (Docker build/run)

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
    ├── com.stano.java-library (extends java, adds publishing)
    └── com.stano.spring-boot (alongside java for Spring apps)

Optional (any project):
    com.stano.sonar
    com.stano.docker
    com.stano.docker-compose
    com.stano.docker-run
```

## Getting Started

### 1. Enable `com.stano.settings` in `settings.gradle.kts`

This **must** be the first plugin block, before `rootProject.name`:

```kotlin
plugins {
  id("com.stano.settings") version "0.1.0"  // replace it with actual version
}

rootProject.name = "my-app"

// Configure the build cache prefix (optional; defaults to rootProject.name)
buildCacheSettings {
  buildCachePrefix.set("my-custom-prefix")
}

// Configure private Maven repository URL (optional)
// Can also be set via gradle.properties or env var STANO_MAVEN_URL
```

This plugin:
- Configures `dependencyResolutionManagement` to use `mavenLocal()` and `mavenCentral()` by default
- Optionally adds a private Maven repository if `STANO_MAVEN_URL` is configured
- Optionally enables S3 build cache (set `com.stano.build-cache.type=s3`)
- Automatically pins Kotlin JVM and all `com.stano.*` plugins to compatible versions

### 2. Apply `com.stano.application` or `com.stano.library` to the Root Project

**For applications** (auto-versioned from git):

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.application") version "0.1.0"
}
```

**For libraries** (no auto-versioning):

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.library") version "0.1.0"
}
```

### 3. Apply `com.stano.java` (or `com.stano.java-library`) to Subprojects

```kotlin
// build.gradle.kts (each Java/Kotlin subproject)
plugins {
  id("com.stano.java") version "0.1.0"
}

// For a library that publishes to Maven:
// id("com.stano.java-library") version "0.1.0"
```

---

## Plugin Reference

### `com.stano.settings`

**Applied to:** `settings.gradle.kts`
**Description:** Configures repository resolution, build cache, and pins plugin versions.

**Minimal example:**

```kotlin
plugins {
  id("com.stano.settings") version "0.1.0"
}

rootProject.name = "my-project"

buildCacheSettings {
  buildCachePrefix.set("my-org")
}
```

**Extension: `buildCacheSettings`**

| Property | Type | Default | Purpose |
|----------|------|---------|---------|
| `buildCachePrefix` | `Property<String>` | `rootProject.name` | S3 key prefix for remote build cache |

**Properties (Gradle property → environment variable)**

| Gradle Property | Env Variable | Purpose | Required? |
|---|---|---|---|
| `com.stano.maven.url` | `STANO_MAVEN_URL` | Private Maven repository URL | No (optional) |
| `com.stano.maven.username` | `STANO_MAVEN_USERNAME` | Maven credentials | If private repo needs auth |
| `com.stano.maven.password` | `STANO_MAVEN_PASSWORD` | Maven credentials | If private repo needs auth |
| `com.stano.build-cache.type` | `STANO_BUILD_CACHE_TYPE` | Set to `s3` to enable remote build cache | No (default: local only) |
| `com.stano.build-cache.local.enabled` | — | Enable/disable local build cache | No (default: `true`) |
| `com.stano.build-cache.s3.bucket` | `STANO_BUILD_CACHE_S3_BUCKET` | S3 bucket name for cache | If using S3 cache |
| `com.stano.build-cache.s3.region` | `STANO_BUILD_CACHE_S3_REGION` | AWS region | If using S3 cache |
| `com.stano.build-cache.s3.access-key-id` | `STANO_BUILD_CACHE_S3_ACCESS_KEY_ID` | AWS access key | If using S3 cache |
| `com.stano.build-cache.s3.secret-access-key` | `STANO_BUILD_CACHE_S3_SECRET_ACCESS_KEY` | AWS secret key | If using S3 cache |
| `com.stano.build-cache.push-enabled` | `STANO_BUILD_CACHE_PUSH_ENABLED` | Allow pushing to S3 cache | No (default: `false`) |

---

### `com.stano.base`

**Applied to:** Root project only
**Description:** Registers `BaseExtension` on the root project with cross-project configuration (Java version, versioning, CI metadata, Docker/Pact broker coordinates). Also registers `jacocoRootReport` task to aggregate coverage from all subprojects.

**Minimal example:**

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.base") version "0.1.0"
}

// (Optional) Configure via root extension:
extensions.getByType<BaseExtension>().apply {
  javaVersion.set("21")  // or read from system
  mspVersion.set("2.0.0")
  contextName.set("my-service")
  dockerRegistryHost.set("docker.mycompany.com")
}
```

**Extension: `root` (type `BaseExtension`)**

| Property | Type | Default | Source | Purpose |
|----------|------|---------|--------|---------|
| `javaVersion` | `String` | `"21"` | `javaVersion` prop / env | Java language version for toolchain |
| `mspVersion` | `String` | `null` | `mspVersion` prop | Internal MSP BOM version |
| `contextName` | `String` | `null` | `contextName` prop | Application/service logical name |
| `buildNumber` | `String` | `null` | `BUILD_NUMBER` env | CI build number |
| `buildTime` | `LocalDateTime` | now (Chicago TZ) | computed | Build timestamp |
| `pactBrokerUrl` | `String` | `null` | `pactBrokerUrl` prop / env | Pact contract broker URL |
| `pactBrokerUsername` | `String` | `null` | `pactBrokerUsername` prop / env | Pact broker credentials |
| `pactBrokerPassword` | `String` | `null` | `pactBrokerPassword` prop / env | Pact broker credentials |
| `pactBrokerToken` | `String` | `null` | `pactBrokerToken` prop / env | Pact broker auth token |
| `dockerRegistryHost` | `String` | `null` | `dockerRegistryHost` prop / env | Docker registry hostname |
| `dockerRegistryUsername` | `String` | `null` | `dockerRegistryUsername` prop / env | Docker registry credentials |
| `dockerRegistryPassword` | `String` | `null` | `dockerRegistryPassword` prop / env | Docker registry credentials |
| `dockerRegistryAwsProfile` | `String` | `null` | `dockerRegistryAwsProfile` prop / env | AWS profile for ECR login |
| `branchNameProvider` | `Provider<String>` | auto-computed | Git/CI env | Current branch name |
| `commitHashProvider` | `Provider<String>` | auto-computed | Git HEAD | Abbreviated commit hash |
| `commitTimeProvider` | `Provider<String>` | auto-computed | Git HEAD | Commit timestamp |
| `repositoryUrlProvider` | `Provider<String>` | auto-computed | Git remote | Git repository URL |
| `repositoryOrganizationProvider` | `Provider<String>` | auto-computed | parsed from URL | Organization from git URL |

**Tasks registered:**

| Task | Type | Purpose |
|------|------|---------|
| `jacocoRootReport` | `JacocoReport` | Aggregates JaCoCo exec files from all subprojects; produces HTML and XML reports in `build/reports/jacoco/` |

---

### `com.stano.application`

**Applied to:** Root project only
**Extends:** `com.stano.base`
**Description:** Everything `com.stano.base` does, plus automatically sets `project.version` for all subprojects based on git metadata (commit timestamp + hash, optionally with CI build number).

**Minimal example:**

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.application") version "0.1.0"
}
```

The version is computed as:
- With git + build number: `20250615120000-a1b2c3d4-123` (timestamp-hash-buildNumber)
- With git only: `20250615120000-a1b2c3d4` (timestamp-hash)
- Fallback (no git): `20250615120000` (build time in `yyyyMMddHHmmss`)

---

### `com.stano.library`

**Applied to:** Root project only
**Extends:** `com.stano.base`
**Description:** Like `com.stano.application` but does NOT set `project.version`. Use for multi-module library builds where version is managed elsewhere (e.g., in `gradle.properties` or a parent POM).

**Minimal example:**

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.library") version "0.1.0"
}

version = "1.2.3"  // manage version yourself
```

---

### `com.stano.java`

**Applied to:** Each Java/Kotlin subproject
**Prerequisite:** `com.stano.base` (or `com.stano.application`/`com.stano.library`) must be applied to the root project
**Description:** Core Java subproject plugin. Configures compilers (Java toolchain + incremental), applies Spotless (Google Java Format), configures test execution (JUnit Platform, Pact broker properties), manages JaCoCo coverage.

**Minimal example:**

```kotlin
// build.gradle.kts (subproject)
plugins {
  id("com.stano.java") version "0.1.0"
}

dependencies {
  implementation("com.example:my-lib:0.1.0")
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}
```

**Extension: `javaConventions` (marker, no properties)**

**Features applied:**

- Applies `java-library`, `org.jetbrains.kotlin.jvm`, `jacoco`
- Spotless with Google Java Format 1.35.0 (enforced on `check` task)
- Java compiler: toolchain from `javaVersion` (default `"21"`), incremental compilation, forked JVM with `Xmx4096m`
- Kotlin compiler: incremental, same free compiler args as Java
- Test execution: `useJUnitPlatform()`, `Xmx4096m`/`Xms512m` heap, JVM args for reflective access
- JaCoCo: all `test` tasks finalize with `jacocoTestReport`; reports in `build/reports/jacoco/`
- Automatic MSP BOM inclusion (when `mspVersion` is set):
  - Excludes `commons-logging`, `log4j` globally
  - Adds `com.stano:msp-bom` as enforced platform
  - Adds `org.jetbrains:annotations` as `compileOnly`
  - Adds `com.stano:msp-test-starter` as `testImplementation`
- Automatic mapstruct processor: if `org.mapstruct:mapstruct` is detected in any dependency configuration, `org.mapstruct:mapstruct-processor` is auto-added to `annotationProcessor`

**Tasks registered:**

| Task | Type | Purpose |
|------|------|---------|
| `spotlessCheck` | (Spotless) | Verify code formatting (wired to `check`) |
| `spotlessApply` | (Spotless) | Auto-fix code formatting |
| `test` | `Test` | Run all JUnit 5 tests with coverage |
| `jacocoTestReport` | `JacocoReport` | Generate HTML/XML coverage reports |

**Test system properties** (set automatically):

| Property | Value |
|----------|-------|
| `pactBrokerUrl` | From `BaseExtension.pactBrokerUrl` |
| `pactBrokerUsername` | From `BaseExtension.pactBrokerUsername` |
| `pactBrokerPassword` | From `BaseExtension.pactBrokerPassword` |
| `pact.provider.version` | `project.version` |
| `pact.provider.branch` | Current branch name |

---

### `com.stano.java-library`

**Applied to:** Library subprojects that publish to Maven
**Extends:** `com.stano.java` (plus `maven-publish`)
**Description:** Adds sources + Javadoc JARs, configures Maven publishing to the private Stano repository.

**Minimal example:**

```kotlin
// build.gradle.kts (library subproject)
plugins {
  id("com.stano.java-library") version "0.1.0"
}

// Optional: prefix artifact IDs for disambiguation in multi-module builds
// Set via gradle.properties or -P flag:
// artifactIdPrefix = "myorg"
```

**Artifacts published to Maven:**

- `{name}.jar` (compiled classes)
- `{name}-sources.jar` (source code)
- `{name}-javadoc.jar` (Javadoc)

**Maven publishing:**

- Repository: `com.stano.maven.url` (from `com.stano.settings`)
- Credentials: `com.stano.maven.username` / `com.stano.maven.password`
- Authentication: HTTP header (compatible with private Maven with header-based auth)

**Optional property:**

| Property | Type | Purpose |
|----------|------|---------|
| `artifactIdPrefix` | String (project property) | If set, prefixes artifact IDs as `{prefix}-{projectName}` for disambiguation |

---

### `com.stano.spring-boot`

**Applied to:** Spring Boot application subprojects (alongside `com.stano.java`)
**Prerequisite:** `BaseExtension` must be available on the root project (from `com.stano.base` or `com.stano.application`)
**Description:** Spring Boot integration. Applies Spring Boot plugin, adds Spring + MSP dependencies, copies OTel Java agent JAR, updates `application.yml` with build metadata during resource processing.

**Minimal example:**

```kotlin
// build.gradle.kts (Spring Boot subproject)
plugins {
  id("com.stano.java") version "0.1.0"
  id("com.stano.spring-boot") version "0.1.0"
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
}
```

Also requires an `src/main/resources/application.yml` with an `info.app` section:

```yaml
info:
  app:
    name: My Service
    description: "Service description"
```

**Dependencies auto-added:**

- `developmentOnly`: `org.springframework.boot:spring-boot-devtools`
- `runtimeOnly`: `io.micrometer:micrometer-registry-prometheus`
- `implementation`: `com.stano:msp-spring-boot-application:{mspVersion}`
- `testImplementation`: `com.stano:msp-spring-test-starter:{mspVersion}`

**Tasks registered:**

| Task | Type | Purpose |
|------|------|---------|
| `copyOtelJavaagent` | Copy | Copies any `opentelemetry-javaagent*.jar` from classpath to `build/libs/` |
| `bootJar` | Spring Boot | Packages as executable JAR (archive name = root project name) |

**Build metadata** (automatically written to `application.yml` during `processResources`):

```yaml
info:
  app:
    version: ${project.version}
    name: ${contextName}
  build:
    number: ${BUILD_NUMBER}
    branch: ${BRANCH_NAME}
    job: ${JOB_NAME}
```

---

### `com.stano.sonar`

**Applied to:** Root project (optional, silently skips if not configured)
**Description:** SonarQube integration. Applies SonarQube plugin and configures analysis properties when host and token are provided.

**Minimal example:**

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.sonar") version "0.1.0"
}

// Configure via gradle.properties or environment:
// com.stano.sonar.host = https://sonar.mycompany.com
// STANO_SONAR_TOKEN = squ_abc123...
```

**Configuration properties:**

| Gradle Property | Env Variable | Purpose |
|---|---|---|
| `com.stano.sonar.disabled` | `STANO_SONAR_DISABLED` | Set to `true` to skip Sonar entirely |
| `com.stano.sonar.host` | `STANO_SONAR_HOST` | SonarQube server URL |
| `com.stano.sonar.token` | `STANO_SONAR_TOKEN` | SonarQube authentication token |
| `com.stano.sonar.fail-build-enabled` | `STANO_SONAR_FAIL_BUILD_ENABLED` | Set to `true` to fail build on quality gate failure |
| `sonarProjectName` | — | Override the project name used in Sonar (project property only) |

**Behavior:**

- When `disabled=false` and both `host` and `token` are configured: applies SonarQube plugin with all properties
- When `fail-build-enabled=true`: sets `sonar.qualitygate.wait=true` (waits for quality gate before returning)
- When not configured: prints a warning to stdout (does NOT fail the build)

**SonarQube properties set:**

- `sonar.host.url` = `com.stano.sonar.host`
- `sonar.token` = `com.stano.sonar.token`
- `sonar.projectName` = `sonarProjectName` property or `project.name`
- `sonar.projectKey` = `{project.group}:{projectName}`
- `sonar.projectVersion` = `project.version`

---

### `com.stano.docker`

**Applied to:** Any project (typically root or a subproject with a Dockerfile)
**Description:** Full Docker image build, tag, and push pipeline. Uses `docker buildx build` by default for multi-platform support.

**Minimal example:**

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.docker") version "0.1.0"
}

docker {
  name.set("docker.mycompany.com/my-org/my-service:latest")
  dockerfile.set(file("Dockerfile"))
  buildArgs.put("BASE_IMAGE", "openjdk:21-jdk-slim")
  labels.put("com.example.version", project.version.toString())
}
```

**Extension: `docker` (type `DockerExtension`)**

| Property | Type | Default | Purpose |
|----------|------|---------|---------|
| `name` | `Property<String>` | required | Full Docker image name with tag (e.g., `registry/org/image:v1.0`) |
| `dockerfile` | `Property<File>` | `./Dockerfile` | Path to Dockerfile |
| `buildArgs` | `MapProperty<String,String>` | empty | Docker build args (`--build-arg KEY=VALUE`) |
| `labels` | `MapProperty<String,String>` | empty | Docker image labels (`--label KEY=VALUE`) |
| `buildx` | `Property<Boolean>` | `true` | Use `docker buildx build` (vs `docker build`) |
| `platform` | `SetProperty<String>` | `linux/amd64` | Buildx platforms (e.g., `linux/amd64`, `linux/arm64`) |
| `pull` | `Property<Boolean>` | `false` | Always pull base image (`--pull`) |
| `noCache` | `Property<Boolean>` | `false` | Do not use build cache (`--no-cache`) |
| `network` | `Property<String>` | `null` | Docker network mode (`--network`) |
| `load` | `Property<Boolean>` | `false` | Load image into local Docker daemon (buildx only; mutually exclusive with `push`) |
| `push` | `Property<Boolean>` | `false` | Push to registry (buildx only; mutually exclusive with `load`) |
| `builder` | `Property<String>` | `null` | Buildx builder instance to use |
| `files(Closure)` | `CopySpec` | — | Additional files to include in Docker build context |

**Tasks registered:**

| Task | Type | Group | Purpose |
|------|------|-------|---------|
| `dockerClean` | Delete | Docker | Removes `build/docker/` directory |
| `dockerPrepare` | Copy | Docker | Copies Dockerfile and configured files into `build/docker/` |
| `docker` | Exec | Docker | Runs `docker [buildx] build` with all configured options |
| `dockerTag{Name}` | Exec | Docker | Tags image with a specific tag (one per configured tag) |
| `dockerTag` | Task | Docker | Depends on all `dockerTag*` tasks |
| `dockerPush{Name}` | Exec | Docker | Pushes specific tagged image to registry |
| `dockerTagsPush` | Task | Docker | Aggregates all push tasks |
| `dockerPush` | Task | Docker | Alias for `dockerTagsPush` |
| `dockerImageUrl` | Task | Docker | Writes image name to `build/docker-image-url.txt` |
| `dockerLogin` | Exec | Docker | Logs into Docker registry (auto-detects AWS ECR) |
| `dockerLogout` | Exec | Docker | Logs out of Docker registry |

**Auto-configuration when `com.stano.spring-boot` is also applied:**

- `docker.name` → `{dockerRegistryHost}/{org}/{contextName}/{branch}:{version}`
- `docker.files()` → outputs of `bootWar` task
- Build args: `DOCKER_REGISTRY`, `PROJECT_VERSION`, `CONTEXT_NAME`, `BUILD_NUMBER`
- Labels: `com.stano.build-hostname`, `com.stano.build-username`, `com.stano.repository-url`, `com.stano.branch`, `com.stano.build-number`, `com.stano.commit-hash`, `com.stano.commit-time`

**Example: multi-platform build with push:**

```kotlin
docker {
  name.set("docker.mycompany.com/my-service:${project.version}")
  buildx.set(true)
  platform.set(setOf("linux/amd64", "linux/arm64"))
  push.set(true)
  buildArgs.put("BASE_IMAGE", "eclipse-temurin:21-jdk-alpine")
}

// ./gradlew docker dockerPush
```

---

### `com.stano.docker-compose`

**Applied to:** Any project
**Description:** Template-based Docker Compose file generation. Substitutes dependency versions and custom tokens into a template, then provides tasks to start/stop services.

**Minimal example:**

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.docker-compose") version "0.1.0"
}

dockerCompose {
  template.set(file("docker-compose.yml.template"))
  dockerComposeFile.set(file("docker-compose.yml"))
  templateToken("LOG_LEVEL", "INFO")
}
```

**Template file** (`docker-compose.yml.template`):

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
  app:
    image: my-org/my-app:{{project.version}}
    environment:
      LOG_LEVEL: "{{LOG_LEVEL}}"
      DATABASE_URL: "postgresql://postgres:5432/mydb"
    depends_on:
      - postgres
```

**Extension: `dockerCompose`**

| Property | Type | Default | Purpose |
|----------|------|---------|---------|
| `template` | `Property<File>` | `docker-compose.yml.template` | Path to template file |
| `dockerComposeFile` | `Property<File>` | `docker-compose.yml` | Output file path |
| `templateTokens` | `MapProperty<String,String>` | empty | Token substitutions (`{{key}}` → value) |

**Tasks registered:**

| Task | Type | Group | Purpose |
|------|------|-------|---------|
| `generateDockerCompose` | Generate | Docker | Processes template and writes `docker-compose.yml` |
| `dockerComposeUp` | Exec | Docker | Runs `docker compose up -d` |
| `dockerComposeDown` | Exec | Docker | Runs `docker compose down` |

**Token format:**

- `{{group:artifact}}` → auto-resolved from dependency versions (e.g., `org.postgresql:postgresql` → `15.0`)
- `{{key}}` → replaced from `templateTokens` map
- Undefined tokens cause task failure

---

### `com.stano.docker-run`

**Applied to:** Any project
**Description:** Configures a named Docker container with ports, volumes, environment, and network. Provides tasks to run, stop, inspect, and remove the container.

**Minimal example:**

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.docker-run") version "0.1.0"
}

dockerRun {
  name.set("postgres-test")
  image.set("postgres:15")
  ports.set(setOf("5432:5432"))
  env.put("POSTGRES_PASSWORD", "testpass")
  daemonize.set(true)
}

// ./gradlew dockerRun
// ./gradlew dockerRunStatus
// ./gradlew dockerStop
```

**Extension: `dockerRun`**

| Property | Type | Default | Purpose |
|----------|------|---------|---------|
| `name` | `Property<String>` | required | Container name (`docker run --name`) |
| `image` | `Property<String>` | required | Docker image to run |
| `ports` | `SetProperty<String>` | empty | Port mappings (`"host:container"` or `"container"`) |
| `volumes` | `MapProperty<Object,String>` | empty | Volume mounts (`localPath` → `containerPath`) |
| `env` | `MapProperty<String,String>` | empty | Environment variables (`-e KEY=VALUE`) |
| `network` | `Property<String>` | `null` | Docker network (`--network`) |
| `arguments` | `ListProperty<String>` | empty | Extra `docker run` arguments |
| `command` | `ListProperty<String>` | empty | Command to run in container |
| `daemonize` | `Property<Boolean>` | `true` | Run in background (`-d` flag) |
| `clean` | `Property<Boolean>` | `false` | Auto-remove container on exit (`--rm` flag) |
| `ignoreExitValue` | `Property<Boolean>` | `false` | Do not fail build on non-zero exit |

**Tasks registered:**

| Task | Type | Group | Purpose |
|------|------|-------|---------|
| `dockerRun` | Exec | Docker Run | Runs `docker run [options] {image} [cmd]` |
| `dockerRunStatus` | Exec | Docker Run | Prints container state (RUNNING/STOPPED) |
| `dockerNetworkModeStatus` | Exec | Docker Run | Prints network mode |
| `dockerStop` | Exec | Docker Run | Runs `docker stop {name}` |
| `dockerRemoveContainer` | Exec | Docker Run | Runs `docker rm {name}` |

**Example: PostgreSQL for integration tests:**

```kotlin
dockerRun {
  name.set("pg-test")
  image.set("postgres:15-alpine")
  ports.set(setOf("5432"))  // random host port
  volumes.put(project.file("test-db-init.sql"), "/docker-entrypoint-initdb.d/init.sql")
  env.put("POSTGRES_USER", "testuser")
  env.put("POSTGRES_PASSWORD", "testpass")
  env.put("POSTGRES_DB", "testdb")
  daemonize.set(true)
  clean.set(false)
}

test {
  dependsOn("dockerRun")
  finalizedBy("dockerStop")
}
```

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
  id("com.stano.settings") version "0.1.0"
}

rootProject.name = "my-app"
buildCacheSettings { buildCachePrefix.set("my-org") }

include("app", "lib-common")
```

**Root `build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.application") version "0.1.0"
  id("com.stano.sonar") version "0.1.0"
}

extensions.getByType<com.stano.gradle.base.BaseExtension>().apply {
  javaVersion.set("21")
  mspVersion.set("2.0.0")
  contextName.set("my-app")
  dockerRegistryHost.set("docker.mycompany.com")
}
```

**`app/build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.java") version "0.1.0"
  id("com.stano.spring-boot") version "0.1.0"
  id("com.stano.docker") version "0.1.0"
}

dependencies {
  implementation(project(":lib-common"))
  implementation("org.springframework.boot:spring-boot-starter-web")
}

docker {
  dockerfile.set(file("../Dockerfile"))
  buildArgs.put("BASE_IMAGE", "eclipse-temurin:21-jdk-alpine")
}
```

**`lib-common/build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.java-library") version "0.1.0"
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
  id("com.stano.settings") version "0.1.0"
}

rootProject.name = "my-lib-suite"
include("core", "spring-integration", "testing-utils")
```

**Root `build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.library") version "0.1.0"
}

version = "1.2.3"  // managed in gradle.properties or here
```

**`core/build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.java-library") version "0.1.0"
}

dependencies {
  api("com.google.guava:guava:33.0.0-jre")
}
```

**`spring-integration/build.gradle.kts`:**

```kotlin
plugins {
  id("com.stano.java-library") version "0.1.0"
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

---

## Maintenance

### Upgrading the Gradle Wrapper

```bash
./gradlew wrapper --gradle-version <version>
```

### Useful Links

* [Gradle S3 Build Cache Plugin](https://github.com/burrunan/gradle-s3-build-cache)
