# `com.stano.java-library`

Extends [`com.stano.java`](java.md) for library subprojects: adds sources + Javadoc JARs and configures Maven publishing to a private repository. Composes cleanly with [`com.stano.maven-central-publish`](maven-central-publish.md) — see the composability note below.

Implementation class: `com.stano.gradle.javalibrary.JavaLibraryPlugin`.

## Apply it

```kotlin
// build.gradle.kts (library subproject)
plugins {
  id("com.stano.java-library") version "0.1.12"
}
```

## Prerequisites

None to apply directly — it applies `com.stano.java` itself (which in turn requires `com.stano.base` on the root project; see [`com.stano.java`](java.md)) and Gradle's `maven-publish` plugin.

## What it does under the hood

1. Applies `com.stano.java` and `maven-publish`.
2. `JavaPluginExtension.withJavadocJar()` + `.withSourcesJar()`. If the project property `artifactIdPrefix` is set, renames the `sourcesJar`/`javadocJar` archive base names to `<artifactIdPrefix>-<projectName>`. Both jars exclude `**/.gitkeep`.
3. Suppresses the `GenerateModuleMetadata` "enforced-platform" validation warning (`suppressedValidationErrors.add("enforced-platform")`).
4. **In `afterEvaluate`, only if `com.stano.maven-central-publish` is NOT also applied to the project**: configures a `stano-maven` Maven repository and a publication named after the project's jar archive base name, if `com.stano.maven.url` (or `STANO_MAVEN_URL`) resolves. If that URL isn't configured, publishing is silently skipped entirely.
5. Adds `-Xdoclint:none -quiet` to every `Javadoc` task's core options.

No extension of its own.

## Publishing

**Repository** — name: `stano-maven`, URL from `com.stano.maven.url` project property or `STANO_MAVEN_URL` env var. If neither is set, no repository or publication is created at all.

**Credentials** (shared resolution logic, same as [`com.stano.settings`](settings.md)):

1. `CI_JOB_TOKEN` env var (GitLab CI ambient) → header credentials, header `Job-Token`. Always wins.
2. `com.stano.maven.token` / `STANO_MAVEN_TOKEN` → header credentials, header name from `com.stano.maven.token-header` / `STANO_MAVEN_TOKEN_HEADER` (default `Private-Token`).
3. `com.stano.maven.username`/`STANO_MAVEN_USERNAME` + `com.stano.maven.password`/`STANO_MAVEN_PASSWORD` → HTTP Basic.

**Artifacts published**: `<name>.jar`, `<name>-sources.jar`, `<name>-javadoc.jar`.

| Optional property | Type | Purpose |
|---|---|---|
| `artifactIdPrefix` | project property | Prefixes source/javadoc jar names as `<prefix>-<projectName>` |

## Composability with `com.stano.maven-central-publish`

If a project applies **both** `com.stano.java-library` and [`com.stano.maven-central-publish`](maven-central-publish.md), `com.stano.java-library` automatically skips creating its own `stano-maven`-repo publication — only `maven-central-publish`'s `mavenJava` publication exists. This avoids Gradle's "Multiple publications with coordinates '...' will overwrite each other!" duplicate-publication warning that would otherwise occur since both plugins would target the same `group:artifact:version` coordinates. No configuration is needed to get this behavior — it's automatic and order-independent (works whether `java-library` or `maven-central-publish` is applied first in the `plugins {}` block).

## Full example

```kotlin
// lib-common/build.gradle.kts
plugins {
  id("com.stano.java-library") version "0.1.12"
}

dependencies {
  api("com.fasterxml.jackson.core:jackson-databind")
}
```

```bash
# Publish jar + sources + javadoc to the private repo
./gradlew publish
```

## Gotchas

- If `com.stano.maven.url`/`STANO_MAVEN_URL` isn't set, `./gradlew publish` on a `com.stano.java-library`-only project has nothing to publish to — there's no error, it's just a silent no-op.
- If you need both a private-repo publish and a Central publish for the same artifact, apply both plugins — do not try to configure two publications by hand, `java-library` already defers to `maven-central-publish` for you.
