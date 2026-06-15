# gradle-plugins Agent Guidance

## Git Rules

**NEVER run `git add`, `git commit`, or `git push`.**

The developer must always perform these operations manually.

---

## Build & Test

```bash
./gradlew build                          # build all submodules
./gradlew test                           # run all tests with JUnit Platform
./gradlew :gradle-plugins-<name>:build   # build a single submodule
./gradlew :gradle-plugins-<name>:test    # test a single submodule
./gradlew spotlessApply                  # auto-fix all Java formatting issues
./gradlew spotlessCheck                  # verify formatting (runs in CI)
./gradlew sonarqube                      # run SonarQube analysis
```

**Test framework:** JUnit Jupiter 6.1.0. JaCoCo HTML/XML reports are produced automatically after test runs (via `finalizedBy jacocoTestReport`). Never run tests through an IDE runner directly — always use `./gradlew test`.

---

## Environment Setup

Optional environment variables (can also be set via `gradle.properties` or `extraProperties`):

| Variable | Purpose |
|---|---|
| `STANO_SONAR_HOST_URL` | SonarQube host URL |
| `STANO_SONAR_TOKEN` | SonarQube auth token |
| `STANO_MAVEN_URL` | Private Maven repository URL |
| `STANO_MAVEN_USERNAME` / `STANO_MAVEN_PASSWORD` | Maven credentials |
| `STANO_BUILD_CACHE_S3_BUCKET` / `STANO_BUILD_CACHE_S3_REGION` / `STANO_BUILD_CACHE_S3_ACCESS_KEY_ID` / `STANO_BUILD_CACHE_S3_SECRET_ACCESS_KEY` | S3 remote build cache |

---

## Plugin Map

Each `gradle-plugins-*` submodule is an independently published Gradle plugin. The plugin IDs consumers reference are:

| Plugin ID | Implementation Class | Submodule | Purpose |
|---|---|---|---|
| `com.stano.base` | `ProjectPlugin` | `gradle-plugins-base` | Root-project prerequisite. Registers `BaseExtension`, adds `jacocoRootReport`. **Must be applied to the root project before any other stano plugin.** |
| `com.stano.application` | `ApplicationPlugin` | `gradle-plugins-application` | Extends `com.stano.base`. Sets `project.version` from `ProjectVersionProvider`, applies `base` and `jacoco` to the root project. |
| `com.stano.library` | `LibraryPlugin` | `gradle-plugins-library` | Extends `com.stano.base`. Applies `base` and `jacoco` to the root project. For multi-module **library** builds (as opposed to applications). |
| `com.stano.java` | `JavaPlugin` | `gradle-plugins-java` | Core plugin for internal Java/Kotlin modules. Applies java-library, Kotlin JVM, JaCoCo, Spotless (Eclipse formatter). Validates that `com.stano.base` is already on the root. |
| `com.stano.java-library` | `JavaLibraryPlugin` | `gradle-plugins-java-library` | Extends `com.stano.java`. Adds `javadoc` + sources JARs and Maven publishing. |
| `com.stano.spring-boot` | `SpringBootPlugin` | `gradle-plugins-spring-boot` | Applies `org.springframework.boot`, pins Spring Boot + MSP BOM, names the boot JAR after the root project, registers a `copyOtelJavaagent` task. |
| `com.stano.sonar` | `SonarPlugin` | `gradle-plugins-sonar` | SonarQube integration. Silently skips (with a warning) when host/token are unconfigured. |
| `com.stano.settings` | `SettingsPlugin` | `gradle-plugins-settings` | Settings-level plugin. Configures dependency resolution management, S3 build cache, and pins Kotlin JVM plugin version. |
| `com.stano.docker` | `DockerPlugin` | `gradle-plugins-docker` | Docker build support. |
| `com.stano.docker-compose` | `DockerComposePlugin` | `gradle-plugins-docker` | Docker Compose support. |
| `com.stano.docker-run` | `DockerRunPlugin` | `gradle-plugins-docker` | Docker run support. |

**Support libraries (not plugins — consumed by plugin submodules only):**

| Submodule | Purpose |
|---|---|
| `gradle-plugins-base` (testFixtures) | `BasePluginTest` base class for plugin integration tests; also provides shared infrastructure via testFixtures source set |
| `gradle-plugins-bom` | Aggregator BOM re-exported to consumers. Add new `gradle-plugins-*` submodule JARs here as constraints. |

---

## Dependency Management

All versions are centralized in `gradle/libs.versions.toml`. When adding a new dependency:

1. Add a version alias under `[versions]` if the library is not version-catalog-managed yet.
2. Add a library alias under `[libraries]` referencing the version alias.
3. Reference the alias (e.g., `libs.spotless`) in the relevant `build.gradle.kts`.

**Never hardcode version numbers in `build.gradle.kts` files** — use the version catalog. Check `libs.versions.toml` before writing any dependency line.

When adding a new `gradle-plugins-*` submodule, add an `implementation` constraint for its JAR in `gradle-plugins-bom/build.gradle.kts`.

`commons-logging` and `log4j` are globally excluded from all configurations in projects applying `com.stano.java`. Do not add them.

---

## Coding Conventions

### Language

- All plugin implementation is **Java 21**. Do not add Kotlin source files to plugin submodules (Kotlin is for Gradle build scripts only).
- Gradle build scripts are Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`).

### Package Naming

Root package: `com.stano.gradle`. Subpackages match the submodule's functional domain:

| Package | Submodule / purpose |
|---|---|
| `com.stano.gradle.base` | `gradle-plugins-base` — shared plugin infrastructure: `BaseExtension`, `PluginFeature`, `GradlePluginUtil`, `BranchNameProvider`, case-conversion utilities, Jackson/JGit/SnakeYAML wrappers |
| `com.stano.gradle.base.changecase` | Case-conversion utilities |
| `com.stano.gradle.application` | `gradle-plugins-application` |
| `com.stano.gradle.docker` | `gradle-plugins-docker` |
| `com.stano.gradle.javalibrary` | `gradle-plugins-java-library` |
| `com.stano.gradle.java` | `gradle-plugins-java` |
| `com.stano.gradle.library` | `gradle-plugins-library` |
| `com.stano.gradle.plugin.test` | `gradle-plugins-test` (deprecated; use testFixtures from gradle-plugins-base) |
| `com.stano.gradle.settings` | `gradle-plugins-settings` |
| `com.stano.gradle.sonar` | `gradle-plugins-sonar` |
| `com.stano.gradle.springboot` | `gradle-plugins-spring-boot` |

Feature classes live in a `.features` subpackage of the plugin's package (e.g., `com.stano.gradle.java.features`).

### Code Style

Enforced by `.editorconfig` and Spotless:
- 2-space indentation
- LF line endings, UTF-8 encoding
- No trailing whitespace, final newline required
- No wildcard imports (expanded by Spotless `expandWildcardImports()`)
- No unused imports (removed by Spotless `removeUnusedImports()`)

Run `./gradlew spotlessApply` to auto-fix formatting before committing.

### Design Patterns

**`PluginFeature` interface** (`gradle-plugins-util/.../PluginFeature.java`): all feature decomposition classes implement `void apply(Project project)`. Plugin classes instantiate features with `new XyzFeature().apply(project)` — no DI framework.

**Feature decomposition**: plugin logic is split into small, single-concern `PluginFeature` classes in a `.features` subpackage. Do not put all logic directly in the plugin `apply()` method.

**Applying external plugins from a feature**: use `project.getPlugins().apply(SomePlugin.class)` when the class is on the classpath, or `project.getPlugins().apply("plugin.id")` when only the ID is known.

**Configuring Gradle extensions from a feature**: use `project.getExtensions().configure(SomeExtension.class, ext -> { ... })`.

**`BaseExtension`** (`gradle-plugins-base/.../BaseExtension.java`): registered on the root project as `"base"` by `BaseExtensionFeature`. Carries cross-project config: `javaVersion` (default `"21"`), `mspVersion`, `contextName`, pact broker coordinates, Docker registry coordinates, `branchNameProvider`, `commitHashProvider`, etc. Fetch it in features via `project.getRootProject().getExtensions().getByType(BaseExtension.class)`.

**`JavaExtension`** (`gradle-plugins-java/.../JavaExtension.java`): registered on each subproject as `"java"` by `JavaPlugin`. Currently a marker interface — the extension point for future per-project java configuration.

**Prerequisite enforcement**: `JavaPlugin` throws `GradleException` at `apply()` time if `ProjectPlugin` is not already on the root project. Follow this pattern for any plugin that depends on another.

### Test Conventions

- **Class naming**: `<Subject>Test` — e.g., `JavaPluginTest`, `ConfigureCompilersFeatureTest`, `BaseExtensionTest`
- **Base class**: plugin integration tests extend `BasePluginTest` (available via `testFixtures(project(":gradle-plugins-base"))`), which creates a temp-dir `rootProject` + `childProject` via `ProjectBuilder` and applies `BaseExtensionFeature` to the root
- **Method naming**: long descriptive camelCase sentences — e.g., `applyingThePluginShouldRegisterTheSpotlessTask`
- **Assertions**: AssertJ (`assertThat(...).isEqualTo(...)`) and JUnit 5 assertions
- **Mocking**: Mockito 5
- **Network-dependent tests**: annotate with `@Disabled` if they require Maven resolution or external services

---

## Critical Constraints

**Do not edit generated files.** Build outputs in `build/`, generated sources in `src/generated/` are overwritten by Gradle tasks.

**Inactive submodules in `settings.gradle.kts` are intentionally disabled.** Do not uncomment them without an explicit user request.

**Publish to the Gradle Plugin Portal** with `./gradlew publishPlugins`. Credentials (`gradle.publish.key` / `gradle.publish.secret`) must be set in `~/.gradle/gradle.properties`. Obtain keys from https://plugins.gradle.org/u/<username>/keys after logging in. The private Maven publishing (via `stano-maven`) coexists with Portal publishing.

**`gradle-plugins-docker` is excluded from `gradle-plugins-bom`** intentionally. Do not add it back without an explicit user request.
