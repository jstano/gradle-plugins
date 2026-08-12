# `com.stano.spring-boot`

Spring Boot integration for application subprojects. Applies the Spring Boot Gradle plugin, pins Spring Boot + the MSP BOM, names the boot JAR after the root project, packages the OpenTelemetry Java agent, and writes build metadata into `application.yml`.

Implementation class: `com.stano.gradle.springboot.SpringBootPlugin`.

## Apply it

```kotlin
// build.gradle.kts (Spring Boot subproject)
plugins {
  id("com.stano.java") version "0.1.12"
  id("com.stano.spring-boot") version "0.1.12"
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
}
```

## Prerequisites

No explicit `GradleException` guard in this plugin's own code, but it unconditionally reads `root` (`BaseExtension`) off the root project — so `com.stano.base` (or `com.stano.application`/`com.stano.library`) **must** already be applied to the root project, or Gradle throws `UnknownDomainObjectException` looking up the extension. It only requires plain `java` (not `com.stano.java`) on the current project, though in practice you'll apply `com.stano.java` too.

## `application.yml` requirement

Your `src/main/resources/application.yml` must have `info.app` present, or `processResources` throws `GradleException` (`"...is missing 'info' key"` / `"...is missing 'info.app' key"`). If the file doesn't exist at all, this step silently does nothing (no exception).

```yaml
info:
  app:
    name: My Service
    description: "Service description"
```

## What it does under the hood

`SpringBootPlugin.apply()`:

1. **Build info** — on `processResources.doLast`, rewrites `build/resources/main/application.yml`:
   ```yaml
   info:
     app:
       version: ${project.version}
       name: ${root.contextName}       # only if contextName is set
     build:
       number: ${CI_PIPELINE_IID / GITHUB_RUN_NUMBER / BUILD_NUMBER / "unspecified"}
       branch: ${CI_COMMIT_BRANCH / GITHUB_REF_NAME / CHANGE_BRANCH / BRANCH_NAME / "unspecified"}
       job:    ${CI_JOB_NAME / GITHUB_JOB / JOB_NAME / "unspecified"}
   ```
2. **OTel Java agent** — creates an `otelAgent` configuration with `com.stano:msp-bom:<mspVersion>` (enforced platform) + `io.opentelemetry.javaagent:opentelemetry-javaagent`. Registers `verifyOtelJavaagent` (fails the build if the agent jar isn't resolvable) and `copyOtelJavaagent` (copies it into `build/otel`), wires both into `assemble` and `bootJar`.
3. Applies `org.springframework.boot`.
4. Adds dependencies, all versioned via `com.stano:msp-bom:<mspVersion>` enforced platform:
   - `developmentOnly`: `org.springframework.boot:spring-boot-devtools`
   - `runtimeOnly`: `io.micrometer:micrometer-registry-prometheus`
   - `implementation`: `com.stano:msp-spring-boot-application:<mspVersion>`
   - `testImplementation`: `com.stano:msp-spring-test-starter:<mspVersion>`
5. Configures `bootJar`: `archiveBaseName = rootProject.name`, `duplicatesStrategy = FAIL`, `dependsOn(copyOtelJavaagent)`.

No extension of its own — reads `root.mspVersion` and `root.contextName` from the root project's `BaseExtension` (see [`com.stano.base`](base.md)).

## Tasks

| Task | Type | Purpose |
|---|---|---|
| `verifyOtelJavaagent` | (validation) | Fails the build if the OTel javaagent jar isn't resolvable in the `otelAgent` configuration |
| `copyOtelJavaagent` | `Copy` | Copies the resolved OTel javaagent jar into `build/otel`; wired into `assemble` and `bootJar` |
| `bootJar` (configured) | `BootJar` | Archive base name = root project name; `FAIL` on duplicate entries; depends on `copyOtelJavaagent` |

## Gotchas

- `mspVersion` **must** be set on `root` (the root `BaseExtension`) — the dependency coordinates interpolate it directly, so an unset version silently produces a broken coordinate like `com.stano:msp-bom:null`.
- This module does not itself contain any Docker-image-naming logic — that behavior lives in [`com.stano.docker`](docker.md), which detects `com.stano.spring-boot` (via `hasPlugin(...)`) and configures a default image name/build context/labels when both are applied together. Apply `com.stano.docker` too if you want that.
