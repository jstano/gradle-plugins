# `com.stano.sonar`

SonarQube integration. Applies the SonarQube Gradle plugin and configures analysis properties — but only when a host and token are actually configured; otherwise it degrades gracefully instead of failing the build.

Implementation class: `com.stano.gradle.sonar.SonarPlugin` (package-private — reference it only via the plugin ID, not the class directly).

## Apply it

```kotlin
// build.gradle.kts (root project, typically)
plugins {
  id("com.stano.sonar") version "0.1.12"
}
```

```properties
# gradle.properties, or pass as env vars instead
sonar.host.url=https://sonar.mycompany.com
sonar.token=squ_abc123...
```

## Prerequisites

None — safe to apply unconditionally, including in environments (like local dev) where SonarQube isn't configured at all.

## Configuration

| Gradle property | Env variable | Purpose |
|---|---|---|
| `sonar.host.url` | `SONAR_HOST_URL` | SonarQube server URL |
| `sonar.token` | `SONAR_TOKEN` | SonarQube authentication token |

> Use `SONAR_HOST_URL`/`SONAR_TOKEN` — **not** `STANO_SONAR_HOST_URL`/`STANO_SONAR_TOKEN`. There is no `STANO_`-prefixed variant read by this plugin.

## What it does under the hood

1. Resolves `host` from `sonar.host.url` project property, falling back to env `SONAR_HOST_URL`. Resolves `token` the same way from `sonar.token`/`SONAR_TOKEN`.
2. **If either is missing**: logs a warning and returns — `org.sonarqube` is never applied, so no `sonarqube` task exists in the project at all. The build does **not** fail.
3. **If both are present**: applies `org.sonarqube`, then sets these properties on the `sonar` extension:
   - `sonar.host.url`, `sonar.token` — as resolved above
   - `sonar.projectName` = `project.name`
   - `sonar.projectKey` = `<project.group>:<project.name>`
   - `sonar.projectVersion` = `String.valueOf(project.version)`
4. Copies through any additional extra project property whose key starts with `sonar.` and whose value is a `String` — so you can set further Sonar analysis properties (e.g. `sonar.exclusions`) directly as project properties and have them picked up automatically.
5. For every subproject that has both a Sonar extension and the `java` plugin applied, appends `build/generated/sources/annotationProcessor/java/main` to `sonar.sources` if that directory exists — so MapStruct/Lombok/etc. generated code is included in analysis. This only has an effect once `compileJava` has actually run and produced generated sources; run `compileJava` before `sonarqube` if you rely on this.

No custom extension — configures the third-party `org.sonarqube.gradle.SonarExtension`.

## Full example

```kotlin
plugins {
  id("com.stano.sonar") version "0.1.12"
}

// Optional: pass through extra Sonar properties directly
ext["sonar.exclusions"] = "**/generated/**"
```

```bash
SONAR_HOST_URL=https://sonar.mycompany.com SONAR_TOKEN=squ_abc123 ./gradlew sonarqube
```

## Gotchas

- If you expect `sonarqube` to appear as a task but it doesn't, check that both `sonar.host.url`/`SONAR_HOST_URL` and `sonar.token`/`SONAR_TOKEN` are actually resolvable — the plugin skips silently (with only a log warning) rather than failing loudly.
- Generated-sources inclusion in `sonar.sources` only works if `sonar.sources` is already a `Collection` on the extension — if some other configuration replaced it with a non-collection value, this feature warns and does nothing rather than erroring.
