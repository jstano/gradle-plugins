# `com.stano.docker`

Full Docker image build/tag/push pipeline. Uses `docker buildx build` by default for multi-platform support, with automatic AWS ECR login and, when [`com.stano.spring-boot`](spring-boot.md) is also applied, sensible default image naming and build context.

Implementation class: `com.stano.gradle.docker.DockerPlugin`.

## Apply it

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.docker") version "0.1.12"
}

docker {
  name = "docker.mycompany.com/my-org/my-service:latest"
  setDockerfile(file("Dockerfile"))
  buildArgs(mapOf("BASE_IMAGE" to "eclipse-temurin:21-jdk-alpine"))
  labels(mapOf("com.example.version" to project.version.toString()))
}
```

## Prerequisites

None to apply. `docker.name` is required at *task-execution* time (not configuration time) unless `com.stano.spring-boot` is also applied (which supplies a default) — see below.

## Extension: `docker` (type `DockerExtension`)

**Important:** this is a plain Java class with a **mix of JavaBean setters and fluent (non-`setX`) methods** — it is *not* built on Gradle's `Property<T>`/`MapProperty<T>` API, so don't use `.set(...)`/`.put(...)`. Use `=` assignment only where a matching getter+setter pair exists; otherwise call the method directly:

| Property | How to set it (Kotlin DSL) | Type | Default | Purpose |
|---|---|---|---|---|
| `name` | `name = "registry/org/image:tag"` | `String` | none — required (or supplied by Spring Boot integration) | Full image name with tag |
| `dockerfile` | `setDockerfile(file("Dockerfile"))` | `File` | `./Dockerfile` | Path to the Dockerfile |
| `dependsOn` | `dependsOn(someTask)` | vararg `Task` | empty | Extra task dependencies for the `docker` build task |
| `files` | `files("extra-dir", "extra-file.txt")` | vararg `Object` (`CopySpec`) | empty | Extra files/dirs added to the Docker build context |
| `tags` *(deprecated)* | `tags("v1", "v2")` | vararg `String` | empty | Extra tags; `project.version` is always unioned in automatically (if it doesn't contain `:`/`/`) |
| `tag(name, value)` | `tag("prod", "registry/image:prod")` | `(String, String)` | — | Named-task tag: registers a `dockerTag<name>`/`dockerPush<name>` task pair |
| `labels` | `labels(mapOf("k" to "v"))` | `Map<String,String>` | falls back to auto-generated provenance labels unless called explicitly | Docker `--label` values |
| `buildArgs` | `buildArgs(mapOf("K" to "V"))` | `Map<String,String>` | empty | Docker `--build-arg` values |
| `pull` | `pull(true)` | `boolean` | `false` | Adds `--pull` (buildx builds always add `--pull` regardless) |
| `noCache` | `noCache(true)` | `boolean` | `false` | Adds `--no-cache` |
| `network` | `network = "host"` | `String` | `null` | `--network` value |
| `buildx` | `buildx(false)` | `boolean` | `true` | Use `docker buildx build` (vs plain `docker build`) |
| `platform` | `platform("linux/amd64", "linux/arm64")` | vararg `String` | `linux/amd64` if empty | `--platform` values (buildx only) |
| `load` | `load(true)` | `boolean` | `true` unless `push` is set | buildx `--load` |
| `push` | `push(true)` | `boolean` | `false` | buildx `--push`; **mutually exclusive with `load`** |
| `builder` | `builder("mybuilder")` | `String` | `null` | buildx `--builder` name |

> Never capture the `docker` extension instance itself inside a task action (`doFirst`/`doLast`) — it holds a live `Project` reference and is not configuration-cache-safe. Read `docker.name`/`docker.labels` (or `docker.getNameSupplier()`/`getLabelsSupplier()`) into a local variable first.

## What it does under the hood

- `docker.name` throws `IllegalArgumentException("name is a required docker configuration item.")` at command-build time if unset and no Spring Boot default applies.
- `push(true)` **and** `load(true)` together throw `GradleException("cannot combine 'push' and 'load' options")`.
- Label keys are validated against `^[a-z0-9.-]*$` — an invalid key throws `GradleException("Docker label '<k>' contains illegal characters...")`.
- Registers a `docker` `Configuration` and a `DockerComponent` software component (artifact = the packaged Dockerfile) so other projects can depend on `configuration: "docker"`.

## Tasks

| Task | Type | Purpose |
|---|---|---|
| `dockerClean` | `Delete` | Removes `build/docker` |
| `dockerPrepare` | `Copy` | Copies the Dockerfile + configured files into `build/docker` |
| `docker` | `Exec` | Runs `docker build` / `docker buildx build` |
| `dockerTag<Name>` (per tag) | `Exec` | `docker tag <image> <tagName>` |
| `dockerTag` | (lifecycle) | Depends on all `dockerTag<Name>` tasks |
| `dockerPush<Name>` (per tag) | `Exec` | `docker push <tagName>` |
| `dockerTagsPush` | (lifecycle) | Depends on `dockerLogin`, all `dockerPush<Name>` tasks; `finalizedBy(dockerLogout)` |
| `dockerPush` | (lifecycle) | Alias for `dockerTagsPush` |
| `dockerImageUrl` | (task) | Writes the resolved image name to `build/docker-image-url.txt` |
| `dockerLogin` | `Exec` | Logs into the registry (auto-detects AWS ECR by hostname) |
| `dockerLogout` | `Exec` | Logs out of the registry |
| `dockerCleanupImage` | `Exec` | `docker image rm --force <name>`, runs after `dockerPush` |
| `dockerRemoveImages` | `Exec` | Removes an explicit image list (only registered if `dockerRemoveImages.images` is non-empty) |

## Registry credentials

| Purpose | Project property | Env variable |
|---|---|---|
| Registry host | `com.stano.docker.registry.host` | `STANO_DOCKER_REGISTRY_HOST` |
| Registry username | `com.stano.docker.registry.username` | `STANO_DOCKER_REGISTRY_USERNAME` |
| Registry password | `com.stano.docker.registry.password` | `STANO_DOCKER_REGISTRY_PASSWORD` |

> This is a **separate** mechanism from `root.dockerRegistryHost`/`dockerRegistryUsername`/`dockerRegistryPassword` on `com.stano.base`'s `BaseExtension` (which uses plain, non-prefixed property/env names and drives the Spring Boot default image name + `dockerRegistryAwsProfile` for ECR). Don't conflate the two — set both if you need registry credentials *and* Spring Boot's default image naming.

## AWS ECR auto-login

If the registry host matches `^\d{12}\.dkr\.ecr\.([a-z0-9-]+)\.amazonaws\.com(\.cn)?$`, `dockerLogin` automatically runs `aws ecr get-login-password --region <region> [--profile <profile>] | docker login --username AWS --password-stdin <host>` instead of a plain `docker login -u/-p`. The AWS profile comes from `root.dockerRegistryAwsProfile` and is validated against `^[A-Za-z0-9_.-]+$` (throws `GradleException("Invalid AWS profile name: <value>")` if invalid — this guards against shell injection since the value is embedded in a `bash -c` string).

## Spring Boot integration

When [`com.stano.spring-boot`](spring-boot.md) is also applied to the project:

- `docker.name` defaults to `<prefix><contextName-lowercase>/<branchName-lowercase>:<projectVersion>`, where `prefix` is empty for local builds or `<registryHost>/<repoOrg>/` for remote ones.
- `bootJar`'s output is added to the Docker build context automatically.
- If `copyOtelJavaagent` exists, its output is added into an `otel/` subfolder of the build context.
- Standard build args are set: `DOCKER_REGISTRY` (if a registry host is configured), `PROJECT_VERSION`, `CONTEXT_NAME`, `BUILD_NUMBER` (if set).

## Default labels

Unless `labels(...)` is called explicitly, every image build gets these provenance labels automatically: `com.stano.build-hostname`, `com.stano.build-username`, `com.stano.repository-url`, `com.stano.branch`, `com.stano.build-number`, `com.stano.commit-hash`, `com.stano.commit-time` — sourced from the root `BaseExtension`'s git providers (see [`com.stano.base`](base.md)).

## Full example: multi-platform build with push

```kotlin
docker {
  name = "docker.mycompany.com/my-service:${project.version}"
  buildx(true)
  platform("linux/amd64", "linux/arm64")
  push(true)
  buildArgs(mapOf("BASE_IMAGE" to "eclipse-temurin:21-jdk-alpine"))
}
```

```bash
./gradlew docker dockerPush
```

## Gotchas

- Don't use `.set(...)`/`.put(...)` — this extension predates Gradle's lazy `Property`/`MapProperty` API; every setter is either a plain JavaBean setter (`name = "..."`) or a direct method call (`pull(true)`).
- `push(true)` without `load(false)` is fine (they default to mutually adjusting), but explicitly setting both `push(true)` and `load(true)` fails the build.
- `tags(...)` is deprecated — prefer `tag(taskName, value)` for named, individually-runnable tag tasks.
