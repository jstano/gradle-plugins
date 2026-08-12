# `gradle-plugins-bom`

**Not a Gradle plugin** — a plain dependency-aggregator module. There is no plugin ID and nothing to `apply`. It exists so build tooling that wants *all* the `com.stano.*` plugin JARs on one classpath can depend on a single artifact instead of listing each one.

## What it is

`gradle-plugins-bom`'s `build.gradle.kts` declares `implementation` dependencies on every published plugin project:

```kotlin
dependencies {
  implementation(project(":gradle-plugins-application"))
  implementation(project(":gradle-plugins-maven-central-publish"))
  implementation(project(":gradle-plugins-java-library"))
  implementation(project(":gradle-plugins-java"))
  implementation(project(":gradle-plugins-kotlin"))
  implementation(project(":gradle-plugins-base"))
  implementation(project(":gradle-plugins-library"))
  implementation(project(":gradle-plugins-settings"))
  implementation(project(":gradle-plugins-sonar"))
  implementation(project(":gradle-plugins-spring-boot"))
}
```

`gradle-plugins-docker` is **deliberately excluded** from this list.

## When to use it

Consume `com.stano:gradle-plugins-bom:<version>` as a regular dependency in build tooling or a composite build that needs every plugin JAR available at once. For normal application/library builds, you don't need this at all — just apply the individual `com.stano.*` plugins you need (see [`com.stano.settings`](settings.md) for how versions get pinned automatically).

## Gotcha

Despite the "BOM" name, this is **not** a `java-platform`/version-constraints BOM in the Maven-BOM sense — it uses `implementation`, not `api`/`constraints`. It bundles the actual plugin JARs together rather than just pinning their versions.
