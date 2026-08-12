# `com.stano.library`

Root-project plugin for multi-module **library** builds (as opposed to applications — see [`com.stano.application`](application.md)). Behaves like `com.stano.application` except it does not auto-version the project and defaults dependency locking the other way.

Implementation class: `com.stano.gradle.library.LibraryPlugin extends com.stano.gradle.base.BasePlugin`.

## Apply it

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.library") version "0.1.12"
}

version = "1.2.3" // you manage the version yourself — e.g. here, in gradle.properties, or via a release plugin
```

## Prerequisites

None to apply — same as `com.stano.application`, it applies `com.stano.base`'s behavior itself via inheritance.

## What it does under the hood

`LibraryPlugin.apply()`:

1. Runs everything `com.stano.base` does (registers the `root` extension, applies Spotless anchor + `jacocoRootReport` on root — see [`com.stano.base`](base.md)).
2. Applies Gradle's built-in `base` and `jacoco` plugins.
3. Does **not** set `project.version` — you're responsible for versioning (via `gradle.properties`, a parent POM, a release plugin, etc.).
4. Defaults `root.dependencyLocking` to **`false`** if it hasn't been explicitly set (libraries are consumed by other projects and usually want dependency-resolution flexibility, unlike applications).

No extension of its own — configure the inherited `root` extension (see [`com.stano.base`](base.md)).

## Full example

```kotlin
// settings.gradle.kts
plugins {
  id("com.stano.settings") version "0.1.12"
}
rootProject.name = "my-lib-suite"
include("core", "spring-integration")
```

```kotlin
// build.gradle.kts (root project)
plugins {
  id("com.stano.library") version "0.1.12"
}

version = "1.2.3"
```

```kotlin
// core/build.gradle.kts
plugins {
  id("com.stano.java-library") version "0.1.12"
}

dependencies {
  api("com.google.guava:guava:33.0.0-jre")
}
```

## Gotchas

- Unlike `com.stano.application`, this plugin never overwrites `project.version` — if you don't set it yourself, subprojects fall back to Gradle's default `"unspecified"`.
- If you want dependency locking on for a library build, set `root.dependencyLocking = true` explicitly.
