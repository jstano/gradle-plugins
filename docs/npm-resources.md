# `com.stano.npm-resources`

Embeds an npm build's output into a Java jar's resources, so the built frontend is served alongside the Java application. Layers on top of [`com.stano.npm`](npm.md) (auto-applied — you don't need to apply it separately).

Implementation class: `com.stano.gradle.npm.NpmResourcesPlugin` (package-private — reference it only via the plugin ID).

## Apply it

```kotlin
// build.gradle.kts (a subproject, alongside com.stano.java or com.stano.java-library)
plugins {
  id("com.stano.java-library")
  id("com.stano.npm-resources") version "0.1.12"
}

npm {
  nodeVersion.set("20.11.0")
}

npmResources {
  // outputPath/resourceOutputPath, see below — usually left at their defaults
}
```

## Prerequisites

- Everything [`com.stano.npm`](npm.md) requires (a `package.json`, npm/nvm resolvable).
- To get the `jar`/`test` wiring described below, also apply `com.stano.java`/`com.stano.java-library` (or any plugin that applies the `java` plugin) on the same subproject. Without it, `npmAssemble` is still registered and runnable directly, it's just not wired into anything.

## Extension: `npmResources` (type `NpmResourcesExtension`)

| Property | Type | Default | Resolved from | Purpose |
|---|---|---|---|---|
| `assembleOutputPath` | `Property<String>` | `npmOutputPath` project property → `contextName` project property → `root.contextName` | — | Path segment under `resources/main/public/` that the npm build output is copied into |
| `resourceOutputPath` | `Property<String>` | `""` (unset) | `npmResourceOutputPath` project/system property | Explicit destination for `npmAssemble`'s output, overriding the computed `assembleOutputPath`-based path |

`com.stano.npm`'s own `npm { }` extension (`projectDirectory`, `projectDistRoot`, `useNvm`, `nodeVersion`, `runNpmBuildInIde`, etc.) still applies — see [`docs/npm.md`](npm.md).

## Tasks

| Task | Type | Wired into | Purpose |
|---|---|---|---|
| `npmAssemble` | `NpmAssembleTask` (a `Copy` task) | `jar` (when the `java` plugin is applied) | Copies the npm `dist/` output into `build/resources/main/public/<assembleOutputPath>/<package.json name>` (or `resourceOutputPath` if set); depends on `npmRunBuild` |

Applying this plugin also changes how `com.stano.npm`'s own `npmTest` task is used: when the `java` plugin is applied, `test` depends on `npmTest` (this wiring does not happen under `com.stano.npm` alone).

## What it does under the hood

1. Auto-applies `com.stano.npm` if not already applied.
2. Registers `npmAssemble`, depending on `npmRunBuild`.
3. When the `java` plugin is detected on the project, and `runNpmBuildInIde` allows it (see [`docs/npm.md`](npm.md)'s IDE-skip behavior), wires `jar.dependsOn(npmAssemble)` and `test.dependsOn(npmTest)`.

## Full example

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.java-library")
  id("com.stano.npm-resources") version "0.1.12"
  id("com.stano.sonar") version "0.1.12"
}

npm {
  useNvm.set(true)
  nodeVersion.set("20.11.0")
}
```

```bash
./gradlew build   # jar depends on npmAssemble; test depends on npmTest
```

## Gotchas

- `jar`/`test` are only wired to depend on `npmAssemble`/`npmTest` once the `java` plugin is detected on the project — apply `com.stano.java`/`com.stano.java-library` on the same subproject as `com.stano.npm-resources`.
- If you only need the raw npm build lifecycle (no jar embedding), apply [`com.stano.npm`](npm.md) directly instead — it has no Java coupling at all.
