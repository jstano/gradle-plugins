# `com.stano.java`

Core plugin for internal Java subprojects. Applies `java-library`, configures the compiler toolchain, wires up Spotless (Google Java Format), configures test execution (JUnit Platform + Mockito + Pact system properties), and manages JaCoCo coverage.

Implementation class: `com.stano.gradle.java.JavaPlugin`.

## Apply it

```kotlin
// build.gradle.kts (subproject)
plugins {
  id("com.stano.java") version "0.1.12"
}

dependencies {
  implementation("com.example:my-lib:0.1.0")
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}
```

## Prerequisites

`com.stano.base` (or a plugin that extends it — `com.stano.application`, `com.stano.library`) **must already be applied to the root project**. `JavaPlugin.apply()` checks this at apply time and throws:

```
GradleException: com.stano.java requires com.stano.base (or com.stano.application) to be applied to the root project.
```

## Extension: `javaConventions` (type `JavaExtension`)

Currently an **empty marker interface** with no configurable properties — it exists as a future extension point. You don't need to (and can't) configure anything through it today.

## What it does under the hood

Applied as a sequence of features:

1. **Plugins applied**: `java-library`, `org.barfuin.gradle.jacocolog` (nicer console JaCoCo logging), `jacoco`.
2. **Spotless**: `java { googleJavaFormat("1.35.0").reflowLongStrings().formatJavadoc(true) }`, plus `endWithNewline()`, `expandWildcardImports()`, `importOrder()`, `removeUnusedImports()`, `trimTrailingWhitespace()`. Wires `check.dependsOn("spotlessCheck")`.
3. **Default dependencies** (only if `root.mspVersion` is set on the root project's `BaseExtension`):
   - Excludes `commons-logging:commons-logging`, `commons-logging:commons-logging-api`, `log4j:log4j` from every configuration; excludes `jakarta.transaction:jakarta.transaction-api` from `compileClasspath`.
   - Adds `com.stano:msp-bom:<mspVersion>` as an enforced platform to both `implementation` and `annotationProcessor`.
   - Adds `compileOnly org.jetbrains:annotations`, `testImplementation com.stano:msp-test-starter`, `testRuntimeOnly org.junit.platform:junit-platform-launcher`, `testRuntimeOnly net.bytebuddy:byte-buddy-agent`.
   - After evaluation, auto-adds `annotationProcessor org.mapstruct:mapstruct-processor` if `org.mapstruct:mapstruct` is resolvable on `compileClasspath`.
4. **Compiler**: reads `root.javaVersion` (default `"21"`); throws `GradleException` if the running JDK isn't compatible with that version. Sets the Java toolchain, and configures every `JavaCompile` task: `incremental = true`, `fork = true`, fork JVM args `-Xmx4096m -Dhttp.agent=wtf`, compiler args `-Xlint:none -Xdoclint:none -nowarn -parameters`.
5. **Test execution**: every `Test` task gets `minHeapSize = "512m"`, `maxHeapSize = "4096m"`, JVM args `--add-opens java.base/java.lang=ALL-UNNAMED -Dhttp.agent=wtf -Xshare:off`, `useJUnitPlatform()`. A `doFirst` block locates `mockito-core` on the test classpath and adds it as a `-javaagent` (fails with `GradleException("mockito-core not found on test classpath")` if missing). Sets system properties `pactBrokerUrl`/`pactBrokerUsername`/`pactBrokerPassword` (from project properties), `pact.provider.version` (= `project.version`), `pact.provider.branch` (from `root.branchNameProvider`). `testLogging { events("failed"); exceptionFormat = FULL }`. Adds a listener that logs a lifecycle "Test summary" line. `test.finalizedBy(jacocoTestReport)` if that task exists.
6. **JaCoCo**: `jacocoTestReport.dependsOn(test)`, HTML+XML reports on, `classDirectories` excludes `**/generated/**` plus classes compiled from MapStruct/annotation-processor-generated sources under `build/generated/sources/annotationProcessor/java/main` (including inner classes).
7. **Artifacts**: `jar { zip64 = true; exclude("**/.gitkeep") }`.
8. **Dependency locking**: only if `root.dependencyLocking == true` — calls `project.dependencyLocking.lockAllConfigurations()` with `LockMode.STRICT`.

## Tasks

| Task | Type | Purpose |
|---|---|---|
| `spotlessCheck` | (Spotless) | Verifies formatting; wired into `check` |
| `spotlessApply` | (Spotless) | Auto-fixes formatting |
| `test` | `Test` | Runs JUnit 5 tests via JUnit Platform, with Mockito agent + Pact properties |
| `jacocoTestReport` | `JacocoReport` | HTML + XML coverage report, `finalizedBy` of `test` |

## Dependency locking

```bash
./gradlew dependencies --write-locks   # generate/update gradle.lockfile
```

Enable/disable explicitly with `-Pcom.stano.dependency-locking=true|false`, or via `root.dependencyLocking` (see [`com.stano.base`](base.md)).

## Gotchas

- `javaConventions {}` has nothing to configure — don't look there for compiler/test settings; those come from `root { javaVersion = "..." }` on the root project instead.
- `com.stano.java` does **not** apply Kotlin support — add [`com.stano.kotlin`](kotlin.md) alongside it for Kotlin sources.
- MSP BOM dependencies are only added when `root.mspVersion` is set — omit it entirely if you're not using the Modular Spring Platform.
- Test-time system properties for Pact are always set (even to `null`/empty values) — harmless if you're not using Pact.
