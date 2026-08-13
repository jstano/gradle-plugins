# `com.stano.base`

Root-project prerequisite plugin. Every other `com.stano.*` project plugin (`com.stano.java`, `com.stano.kotlin`, `com.stano.spring-boot`, etc.) requires this to already be applied to the **root** project, either directly or transitively through `com.stano.application`/`com.stano.library` (which both extend it).

Implementation class: `com.stano.gradle.base.BasePlugin`.

## Apply it

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.base") version "0.1.12"
}
```

In practice you'll usually apply `com.stano.application` or `com.stano.library` instead (see their docs) — both apply `com.stano.base`'s behavior automatically. Apply `com.stano.base` directly only if you don't want either of those.

## Prerequisites

None — this is the root of the plugin dependency chain (after `com.stano.settings` at the settings level).

## Extension: `root` (type `BaseExtension`)

**Registered under the DSL name `root`, not `base`.** `BaseExtension` is a plain Java bean (no `Property<T>` wrappers), so in Kotlin DSL you assign values directly:

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.base") version "0.1.12"
}

root {
  javaVersion = "21"
  mspVersion = "2.0.0"
  contextName = "my-service"
  dockerRegistryHost = "docker.mycompany.com"
}
```

| Property | Type | Default | Resolved from | Purpose |
|---|---|---|---|---|
| `javaVersion` | `String` | `"21"` | `javaVersion` project/system property or env `JAVA_VERSION`; a leading non-digit prefix like `jdk-` is stripped | Java toolchain version used by `com.stano.java`/`com.stano.kotlin` |
| `mspVersion` | `String` | `null` | `mspVersion` project property only (no env fallback) | Version of the internal `com.stano:msp-bom` platform, consumed by `com.stano.java` and `com.stano.spring-boot` |
| `contextName` | `String` | `rootProject.name` | `contextName` project property only | Logical application/service name, used in Docker image naming and `application.yml` build info |
| `useNvm` | `boolean` | `false` | `com.stano.use-nvm` project/system property or env | Whether to build via nvm (Node Version Manager) instead of a system-installed npm; consumed by `com.stano.npm` |
| `defaultNodeVersion` | `String` | `"12"` | `com.stano.default-node-version` project/system property or env | Default Node version, consumed by `com.stano.npm` |
| `dependencyLocking` | `Boolean` (nullable) | `null` (unset) | `com.stano.dependency-locking` project/system property or env | Tri-state: `null` means "no explicit override" — `com.stano.application` defaults it to `true`, `com.stano.library` defaults it to `false`, but an explicit value here always wins |
| `buildNumber` | `String` | `null` | `BUILD_NUMBER` project/system property or env | CI build number, folded into `com.stano.application`'s computed version |
| `buildTime` | `LocalDateTime` | now, `America/Chicago` | computed at apply time | Wall-clock build timestamp |
| `buildTimeFormatted` | `String` (read-only) | derived | `yyyyMMddHHmmss`, UTC | Fallback version string when there's no git metadata |
| `pactBrokerUrl` / `pactBrokerUsername` / `pactBrokerPassword` / `pactBrokerToken` | `String` | `null` | matching-named project/system property or env | Pact contract-broker coordinates, consumed by `com.stano.java`'s test configuration |
| `dockerRegistryHost` / `dockerRegistryUsername` / `dockerRegistryPassword` / `dockerRegistryAwsProfile` | `String` | `null` | matching-named project/system property or env | Docker registry coordinates, consumed by `com.stano.docker`'s Spring Boot auto-configuration and AWS ECR login |
| `repositoryUrlProvider` | `RepositoryUrlProvider` | computed | git remote `origin` | Lazy `toString()` — the repository's git remote URL |
| `repositoryOrganizationProvider` | `RepositoryOrganizationProvider` | computed | parsed from remote URL | Lazy `toString()` — org/group parsed out of the remote URL (handles `https://`, `ssh://`, and `git@host:org/repo` forms) |
| `branchNameProvider` | `BranchNameProvider` | computed | see below | Lazy `toString()` — current branch name |
| `commitHashProvider` | `CommitHashProvider` | computed | git HEAD | Lazy `toString()` — abbreviated (8-char) commit SHA, or `null` outside a git repo |
| `commitTimeProvider` | `CommitTimeProvider` | computed | git HEAD | Lazy `toString()` — author commit time, `yyyyMMddHHmmss` in the commit's own timezone |

`branchNameProvider` resolution order: `CI_COMMIT_BRANCH` (GitLab) → `GITHUB_REF_NAME` (GitHub Actions) → `CHANGE_BRANCH` → `BRANCH_NAME` (Jenkins) → current git HEAD branch → `"main"` as a final fallback.

`com.stano.npm`'s `NpmExtension.useNvm`/`nodeVersion` properties default from `useNvm`/`defaultNodeVersion` above — the same pattern `assembleOutputPath` uses with `contextName`.

## What it does under the hood

`BasePlugin.apply()`:

1. Registers the `root` extension (idempotent — a no-op if an extension named `root` already exists, so re-applying is safe).
2. **Root project only:** applies the `com.diffplug.gradle.spotless` plugin (without configuring format rules — Spotless rules are configured by `com.stano.java` on each subproject).
3. **Root project only:** registers the `jacocoRootReport` task.

## Tasks

| Task | Type | Purpose |
|---|---|---|
| `jacocoRootReport` | `JacocoReport` | Aggregates JaCoCo `.exec` data from every subproject with a `test`/`jacocoTestReport` task into one HTML+XML report at the root. Class directories are filtered to exclude `**/generated/**`. If there are no subprojects, it aggregates the root project's own `test`/`jacocoTestReport` output instead. |

## Gotchas

- Configure it as `root { ... }`, **not** `base { ... }` — a common mistake given the plugin ID is `com.stano.base`.
- `mspVersion` and `contextName` only check the Gradle **project property** — they don't fall back to an environment variable or system property the way most other `BaseExtension` fields do.
- `dependencyLocking` is `null` by default; only `com.stano.application`/`com.stano.library` give it a default. If you apply `com.stano.base` directly (without either of those), dependency locking stays unset unless you set it explicitly.
