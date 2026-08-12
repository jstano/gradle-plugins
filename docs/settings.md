# `com.stano.settings`

Settings-level plugin, applied in `settings.gradle.kts`. Configures dependency resolution repositories, the Gradle build cache, and pins the version of every other `com.stano.*` plugin (plus the Kotlin JVM plugin) so consuming projects don't have to specify versions on each `id(...)` line.

Implementation class: `com.stano.gradle.settings.SettingsPlugin` (`Plugin<Settings>` — applies to `Settings`, not a `Project`).

## Apply it

This **must** be the first plugin block in `settings.gradle.kts`, before `rootProject.name`:

```kotlin
plugins {
  id("com.stano.settings") version "0.1.12" // check the latest published version
}

rootProject.name = "my-app"
```

## Prerequisites

None — this is the entry point of the plugin chain.

## Extension: `buildCacheSettings` (type `BuildCacheSettingsExtension`)

A Gradle `Property<String>`-based extension:

```kotlin
buildCacheSettings {
  buildCachePrefix.set("my-org")
}
```

| Property | Type | Default | Purpose |
|---|---|---|---|
| `buildCachePrefix` | `Property<String>` | `settings.rootProject.name` if unset | S3 remote-cache key prefix (used as `<prefix>/` in the S3 build cache config) |

## What it does under the hood

On `gradle.settingsEvaluated`, it:

1. **Configures `dependencyResolutionManagement`** — adds `mavenLocal()`, `mavenCentral()`, and (if configured) a private repository named `stano-maven`.
2. **Configures the build cache** — local cache is always enabled; optionally adds a remote S3 cache.
3. **Pins plugin versions** — reads the bundled, build-time-templated `stano-plugins.properties` resource to determine "this version of `gradle-plugins-settings`", then calls `settings.pluginManagement.plugins.id(...).version(...)` for every `com.stano.*` plugin ID (`base`, `application`, `library`, `java`, `java-library`, `kotlin`, `spring-boot`, `sonar`, `maven-central-publish`, `docker`, `docker-compose`, `docker-run`) at that same version, plus pins `org.jetbrains.kotlin.jvm` to a fixed version. Adds `gradlePluginPortal()` to `pluginManagement.repositories`.

This means consuming subprojects can apply any `com.stano.*` plugin **without a `version(...)`** — the version comes from whichever `gradle-plugins-settings` version was applied in `settings.gradle.kts`.

## Private Maven repository configuration

| Gradle property | Env variable | Purpose | Required? |
|---|---|---|---|
| `com.stano.maven.url` | `STANO_MAVEN_URL` | Private Maven repository URL | No — repo is only added if set |
| `com.stano.maven.username` | `STANO_MAVEN_USERNAME` | Basic-auth credentials | If private repo needs auth and no token is set |
| `com.stano.maven.password` | `STANO_MAVEN_PASSWORD` | Basic-auth credentials | If private repo needs auth and no token is set |
| `com.stano.maven.token` | `STANO_MAVEN_TOKEN` | HTTP header credential value; takes precedence over username/password | If private repo needs header-based auth |
| `com.stano.maven.token-header` | `STANO_MAVEN_TOKEN_HEADER` | HTTP header name the token is sent as | No (default: `Private-Token`) |

> **Running in GitLab CI:** the ambient `CI_JOB_TOKEN` variable (set automatically for every job) is used as an HTTP header credential (header `Job-Token`) and always takes precedence over everything above — no configuration needed.

## Build cache configuration

| Gradle property | Env variable | Purpose | Default |
|---|---|---|---|
| `com.stano.build-cache.local.enabled` | — | Enable/disable the local build cache | `true` |
| `com.stano.build-cache.type` | `STANO_BUILD_CACHE_TYPE` | Set to `s3` to enable the remote S3 cache | local only |
| `com.stano.build-cache.s3.bucket` | `STANO_BUILD_CACHE_S3_BUCKET` | S3 bucket name | required if `type=s3` |
| `com.stano.build-cache.s3.region` | `STANO_BUILD_CACHE_S3_REGION` | AWS region | required if `type=s3` |
| `com.stano.build-cache.s3.access-key-id` | `STANO_BUILD_CACHE_S3_ACCESS_KEY_ID` | AWS access key | required if `type=s3` |
| `com.stano.build-cache.s3.secret-access-key` | `STANO_BUILD_CACHE_S3_SECRET_ACCESS_KEY` | AWS secret key | required if `type=s3` |
| `com.stano.build-cache.push-enabled` | `STANO_BUILD_CACHE_PUSH_ENABLED` | Allow pushing to the S3 cache (not just reading) | `false` |

If `type=s3` is set but bucket/region/access-key/secret can't be resolved, `SettingsPlugin` throws `IllegalStateException` at settings-evaluation time — the build cache configuration is validated eagerly, not lazily.

## Full example

```kotlin
// settings.gradle.kts
plugins {
  id("com.stano.settings") version "0.1.12"
}

rootProject.name = "my-app"

buildCacheSettings {
  buildCachePrefix.set("my-org-my-app")
}

// Everything below can also be set in ~/.gradle/gradle.properties or env vars instead:
// com.stano.maven.url=https://maven.mycompany.com/repository/releases
// com.stano.maven.token=xxxxx
// com.stano.build-cache.type=s3
// com.stano.build-cache.s3.bucket=my-build-cache
// com.stano.build-cache.s3.region=us-east-1

include("app", "lib-common")
```

## Gotchas

- The env var/property lookup precedence is: **env var wins over Gradle property** for the build-cache S3 settings, but for the Maven repo credentials, `CI_JOB_TOKEN` (env) always wins first, then token, then username/password — see [`MavenRepositoryCredentials`](base.md) for the shared precedence logic used across plugins.
- Because plugin version pinning happens in `pluginManagement.plugins`, a consumer subproject's `plugins { id("com.stano.java") }` block must **not** specify a `version(...)` — if it does, it can conflict with the pin from `com.stano.settings`.
