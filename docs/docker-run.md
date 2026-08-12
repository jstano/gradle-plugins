# `com.stano.docker-run`

Configures and runs a named Docker container — ports, volumes, env vars, network — with tasks to start, stop, inspect, and remove it. Commonly used to spin up a database or other dependency for integration tests.

Implementation class: `com.stano.gradle.docker.DockerRunPlugin`.

## Apply it

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.docker-run") version "0.1.12"
}

dockerRun {
  name = "postgres-test"
  image = "postgres:15"
  ports("5432:5432")
  env(mapOf("POSTGRES_PASSWORD" to "testpass"))
}
```

```bash
./gradlew dockerRun
./gradlew dockerRunStatus
./gradlew dockerStop
```

## Prerequisites

None.

## Extension: `dockerRun` (type `DockerRunExtension`)

Plain Java class, no `Property<T>` wrappers:

| Property | How to set it | Type | Default | Purpose |
|---|---|---|---|---|
| `name` | `name = "my-container"` | `String` | **required** | Container name (`docker run --name`) |
| `image` | `image = "postgres:15"` | `String` | **required** | Image to run |
| `network` | `network = "my-net"` | `String` | `null` | `--network` value |
| `command` | `command("--flag", "value")` | vararg `String` | empty | Command appended after the image name |
| `ports` | `ports("5432:5432")` or `ports("5432")` (mapped to itself) | vararg `String` | empty | Port mappings; each numeric part must be in `[1, 65536]` or an `IllegalArgumentException` is thrown |
| `env` | `env(mapOf("KEY" to "VALUE"))` | `Map<String,String>` | empty | `-e KEY=VALUE` entries |
| `arguments` | `arguments("--extra-flag")` | vararg `String` | empty | Extra raw `docker run` args, inserted before the image name |
| `volumes` | `volumes(mapOf(project.file("init.sql") to "/docker-entrypoint-initdb.d/init.sql"))` | `Map<Object,String>` | empty | key = local path (resolved via `project.file()` — must already exist, or `IllegalStateException` is thrown), value = container path |
| `daemonize` | `daemonize = true` | `boolean` | **`true`** | Adds `-d` |
| `clean` | `clean = false` | `boolean` | `false` | Adds `--rm`; when `false`, `dockerRun` is `finalizedBy(dockerRunStatus)` |
| `ignoreExitValue` | `ignoreExitValue = false` | `boolean` | `false` | Sets `Exec.ignoreExitValue` on the `dockerRun` task |

## What it does under the hood

Builds the full `docker run` command line in this order:

```
docker run [-d] [--rm] [--network <net>] [-p host:container ...] [-v localAbs:container ...] [-e K=V ...] [--name <name>] [<extra arguments>] <image> [<command...>]
```

## Tasks

| Task | Type | Purpose |
|---|---|---|
| `dockerRun` | `Exec` | Runs the full `docker run [...]` command above; `finalizedBy(dockerRunStatus)` unless `clean = true` |
| `dockerRunStatus` | `Exec` | `docker inspect --format={{.State.Running}} <name>`; prints RUNNING/STOPPED |
| `dockerNetworkModeStatus` | `Exec` | `docker inspect --format={{.HostConfig.NetworkMode}} <name>`; prints whether it matches `default`, the configured `network`, or neither |
| `dockerStop` | `Exec` (ignores exit value) | `docker stop <name>` |
| `dockerRemoveContainer` | `Exec` (ignores exit value) | `docker rm <name>` |

## Full example: PostgreSQL for integration tests

```kotlin
dockerRun {
  name = "pg-test"
  image = "postgres:15-alpine"
  ports("5432") // random host port
  volumes(mapOf(project.file("test-db-init.sql") to "/docker-entrypoint-initdb.d/init.sql"))
  env(mapOf(
    "POSTGRES_USER" to "testuser",
    "POSTGRES_PASSWORD" to "testpass",
    "POSTGRES_DB" to "testdb"
  ))
  daemonize = true
  clean = false
}

tasks.test {
  dependsOn("dockerRun")
  finalizedBy("dockerStop")
}
```

## Gotchas

- `volumes(...)` requires the local path to already exist on disk at configuration time — a not-yet-generated file (e.g. one another task produces) will fail with `IllegalStateException` unless it's created before this extension is configured.
- Port strings are validated at call time — `ports("70000")` fails immediately with `IllegalArgumentException("Port must be in the range [1,65536]")`, not later at task execution.
