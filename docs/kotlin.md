# `com.stano.kotlin`

Opt-in Kotlin JVM support for Java subprojects. Apply it **alongside** `com.stano.java` on any subproject that has Kotlin sources.

Implementation class: `com.stano.gradle.kotlin.KotlinPlugin`.

## Apply it

```kotlin
// build.gradle.kts (subproject with Kotlin sources)
plugins {
  id("com.stano.java") version "0.1.12"
  id("com.stano.kotlin") version "0.1.12"
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
}
```

## Prerequisites

Applies `com.stano.java` itself if it isn't already applied — which in turn requires `com.stano.base` on the root project (see [`com.stano.java`](java.md)). You can list `com.stano.java` explicitly for clarity (as above) or omit it and let `com.stano.kotlin` apply it transparently; both are equivalent.

## What it does under the hood

1. **Anchors** the Kotlin JVM plugin on the **root** project: `rootProject.plugins.apply("org.jetbrains.kotlin.jvm")` — even though `com.stano.kotlin` is applied to a subproject, not the root. This keeps a single, consistent Kotlin version across the whole build (the version itself comes from `com.stano.settings`'s pin).
2. Applies `com.stano.java` to the current project (if not already applied), then applies `org.jetbrains.kotlin.jvm` to the current project too (verified to happen in that order — Java before Kotlin).
3. Configures every `KotlinCompile` task: `compilerOptions.freeCompilerArgs += ["-Xlint:none", "-Xdoclint:none", "-nowarn", "-parameters"]`, `incremental = true` — mirroring the same flags `com.stano.java` uses for `JavaCompile`.

No extension of its own.

## Tasks

No new tasks — configures the existing `compileKotlin`/`compileTestKotlin` tasks (from `org.jetbrains.kotlin.jvm`) with the compiler flags above.

## Gotchas

- `com.stano.java` alone does **not** apply Kotlin support — you must add `com.stano.kotlin` explicitly to any project with `.kt` sources.
- Applying it re-applies `org.jetbrains.kotlin.jvm` on the root project as a side effect — this is intentional (keeps Kotlin version consistent build-wide) but worth knowing if you inspect `rootProject.plugins` and see Kotlin there despite the root only ever having `com.stano.application`/`com.stano.library` applied directly.
