# `com.stano.docker-compose`

Template-based `docker-compose.yml` generation: substitutes resolved dependency versions and custom tokens into a template file, then provides tasks to bring the composed services up/down.

Implementation class: `com.stano.gradle.docker.DockerComposePlugin`.

## Apply it

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.docker-compose") version "0.1.12"
}

dockerCompose {
  setTemplate(file("docker-compose.yml.template"))
  setDockerComposeFile(file("docker-compose.yml"))
  templateToken("LOG_LEVEL", "INFO")
}
```

## Prerequisites

None.

## Extension: `dockerCompose` (type `DockerComposeExtension`)

Plain Java class, no `Property<T>` wrappers:

| Property | How to set it | Type | Default | Purpose |
|---|---|---|---|---|
| `template` | `setTemplate(file("..."))` (accepts anything `project.file(...)` resolves) | `File` | `docker-compose.yml.template` | Source template file |
| `dockerComposeFile` | `setDockerComposeFile(file("..."))` | `File` | `docker-compose.yml` | Generated output file |
| `templateTokens` | `templateToken("KEY", "value")` (adds one at a time) or `setTemplateTokens(mapOf(...))` (replaces the whole map) | `Map<String,String>` | empty | Extra `{{KEY}}` → value substitutions |

## Template format

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
  app:
    image: my-org/my-app:{{project.version}}
    environment:
      LOG_LEVEL: "{{LOG_LEVEL}}"
```

Token resolution, per line of the template:
- `{{group:artifact}}` — auto-resolved from the `docker` configuration's resolved module versions (e.g. `{{org.postgresql:postgresql}}` → the actual resolved version).
- `{{key}}` — replaced from `templateTokens`.
- Any `{{...}}` token left unresolved after substitution **fails the build**, listing both the unmatched tokens and all known dependency tokens.

## What it does under the hood

1. Creates/reuses a `docker` `Configuration`.
2. If any subproject applies `com.palantir.product-dependency-introspection` (a Palantir-specific plugin), its `productDependencies` configuration is added as a project dependency into the root `docker` configuration. If the current project itself applies that plugin, the `docker` configuration `extendsFrom` its own `productDependencies`. This is a no-op unless that Palantir plugin is present in your build.
3. Registers the three tasks below.

## Tasks

| Task | Type | Purpose |
|---|---|---|
| `generateDockerCompose` | (generate) | Resolves the `docker` configuration's module versions + `templateTokens`, substitutes into the template, writes `dockerComposeFile`. Throws `IllegalStateException` if the template file is missing, or if any `{{...}}` token remains unresolved |
| `dockerComposeUp` | (exec) | `docker-compose -f <dockerComposeFile> up -d` |
| `dockerComposeDown` | (exec) | `docker-compose -f <dockerComposeFile> down` |

Both `dockerComposeUp`/`dockerComposeDown` tee stdout/stderr to the console and throw `GradleException` with the full command + captured output on a non-zero exit.

## Full example

```kotlin
plugins {
  id("com.stano.docker-compose") version "0.1.12"
}

dockerCompose {
  setTemplate(file("docker-compose.yml.template"))
  templateToken("LOG_LEVEL", "INFO")
}
```

```bash
./gradlew generateDockerCompose dockerComposeUp
# ...
./gradlew dockerComposeDown
```

## Gotchas

- An unresolved `{{token}}` fails `generateDockerCompose` outright — double-check spelling against both `templateTokens` and the actual `group:artifact` coordinates on the `docker` configuration.
- This is unrelated to [`com.stano.docker`](docker.md)'s own `docker { }` extension despite the shared `docker` configuration name — `com.stano.docker-compose` doesn't build or push images itself.
