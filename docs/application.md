# `com.stano.application`

Root-project plugin for **applications** (as opposed to libraries — see [`com.stano.library`](library.md)). Extends `com.stano.base`'s behavior (Java-class `extends`, not a separate prerequisite check) and additionally computes `project.version` automatically from git metadata.

Implementation class: `com.stano.gradle.application.ApplicationPlugin extends com.stano.gradle.base.BasePlugin`.

## Apply it

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.application") version "0.1.12"
}
```

## Prerequisites

None to apply — it invokes `BasePlugin`'s behavior itself via inheritance, so you do **not** need to separately apply `com.stano.base` first. (This differs from `com.stano.java`, which does require `com.stano.base` to already be on the root project — see [`com.stano.java`](java.md).)

## What it does under the hood

`ApplicationPlugin.apply()`:

1. Runs everything `com.stano.base` does (registers the `root` extension, applies Spotless anchor + `jacocoRootReport` on root — see [`com.stano.base`](base.md)).
2. Applies Gradle's built-in `base` and `jacoco` plugins.
3. **Sets `project.version`** on the root project and propagates the same version object to every subproject (see "Computed version" below).
4. Defaults `root.dependencyLocking` to **`true`** if it hasn't been explicitly set.
5. **Strips the version segment from every `Jar` task's output filename** (root project and all subprojects) — this includes Spring Boot's `bootJar`, since it extends `Jar`. `build/libs/*.jar` is always `<name>.jar`, never `<name>-<version>.jar`, regardless of what `project.version` resolves to.

No extension of its own — configure the inherited `root` extension (see [`com.stano.base`](base.md)).

## Computed version

The version is a lazy, `Serializable` `ProjectVersionProvider` whose `toString()` resolves as:

- **With git metadata and a CI build number** (`root.buildNumber` set): `<commitTimestamp>-<commitHash>-<buildNumber>`, e.g. `20250615120000-a1b2c3d4-123`
- **With git metadata, no build number**: `<commitTimestamp>-<commitHash>`, e.g. `20250615120000-a1b2c3d4`
- **No git repository found**: falls back to `root.buildTimeFormatted` (`yyyyMMddHHmmss` in UTC), e.g. `20250615120000`

Because this is computed once and cached, `project.version` is stable for the whole build even though it's derived lazily.

`build/libs/*.jar` filenames themselves stay version-free regardless (see "What it does under the hood" above) — `project.version` is instead consumed by other plugins for traceability:

- **`com.stano.docker`**, when applied alongside this plugin, builds its default image tag directly from `project.version` (`<contextName>/<branch>:<projectVersion>`), and also auto-adds `project.version` as an extra tag — this is how the commit hash/timestamp ends up in the Docker tag.
- **`com.stano.spring-boot`** writes it into `application.yml` for Actuator's `/info` build-info block.
- **`com.stano.sonar`** sets `sonar.projectVersion` from it.
- **`com.stano.java`**'s Pact test support uses it as the provider version.

## Full example

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.application") version "0.1.12"
  id("com.stano.sonar") version "0.1.12"
}

root {
  javaVersion = "21"
  mspVersion = "2.0.0"
  contextName = "my-app"
  dockerRegistryHost = "docker.mycompany.com"
  // dependencyLocking defaults to true here; override explicitly if needed:
  // dependencyLocking = false
}
```

## Gotchas

- If you want dependency locking off for an application build, set `root.dependencyLocking = false` explicitly — the plugin's own default (`true`) only applies when the value is still unset at apply time.
- Don't set `project.version` yourself after applying this plugin — it overwrites the version on both the root project and every subproject already-declared at apply time. Configure via `root { }` instead.
