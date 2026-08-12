# `com.stano.maven-central-publish`

Publishes a Java library to Maven Central via the [Central Portal](https://central.sonatype.com) publisher API: generates a complete POM (license/developer/SCM metadata), GPG-signs the publication, zips a staging deployment, and uploads it. Composable alongside [`com.stano.java-library`](java-library.md)'s private-repo publish — both can be applied to the same project.

Implementation class: `com.stano.gradle.mavencentralpublish.MavenCentralPublishPlugin`.

## Apply it

```kotlin
// build.gradle.kts (library subproject)
plugins {
  id("com.stano.java-library") version "0.1.12"          // or plain java-library
  id("com.stano.maven-central-publish") version "0.1.12"
}

mavenCentralPublish {
  componentName = "java"
  pomName = "My Library"
  pomDescription = "A library that does X."
  pomUrl = "https://github.com/myorg/my-library"
  licenseName = "Apache License, Version 2.0"
  licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
  developerId = "myuser"
  developerName = "My Name"
  developerEmail = "me@example.com"
  scmConnection = "scm:git:https://github.com/myorg/my-library.git"
  scmDeveloperConnection = "scm:git:ssh://git@github.com:myorg/my-library.git"
  scmUrl = "https://github.com/myorg/my-library"
}
```

## Prerequisites

None to apply — the plugin applies `maven-publish` and `signing` itself if they aren't already applied. You do need a `SoftwareComponent` to publish (typically from `java-library`, i.e. `componentName = "java"`).

## Extension: `mavenCentralPublish` (type `MavenCentralPublishExtension`)

A plain Java bean (no `Property<T>` wrappers) — assign values directly with `=` in Kotlin DSL.

| Property | Type | Default | Purpose |
|---|---|---|---|
| `componentName` | `String` | **required** | Name of the `SoftwareComponent` to publish from (e.g. `"java"`) |
| `pomName` | `String` | **required** | `<name>` in the generated POM |
| `pomDescription` | `String` | **required** | `<description>` in the POM |
| `pomUrl` | `String` | **required** | `<url>` in the POM |
| `licenseName` | `String` | **required** | `<licenses><license><name>` |
| `licenseUrl` | `String` | **required** | `<licenses><license><url>` |
| `developerId` | `String` | **required** | `<developers><developer><id>` |
| `developerName` | `String` | **required** | `<developers><developer><name>` |
| `developerEmail` | `String` | **required** | `<developers><developer><email>` |
| `scmConnection` | `String` | **required** | `<scm><connection>` |
| `scmDeveloperConnection` | `String` | **required** | `<scm><developerConnection>` |
| `scmUrl` | `String` | **required** | `<scm><url>` |
| `centralTokenPropertyName` | `String` | `"com.stano.maven.central.token"` | Name of the project property that holds the Central Portal bearer token |

Only one license and one developer entry is supported (single `<license>`/`<developer>` block), not a list.

**All 12 fields marked required above are validated at `afterEvaluate` time** — leaving any one `null` or blank fails the build with:

```
GradleException: mavenCentralPublish.<fieldName> must be set on project '<project>' (applied by com.stano.maven-central-publish)
```

`centralTokenPropertyName` is *not* validated at configuration time — it's only consulted lazily when the `publishToMavenCentral` task actually runs.

## What it does under the hood

1. **Extension registration.**
2. **POM + publication** (`afterEvaluate`): creates a `MavenPublication` named `mavenJava` from the configured component; sets name/description/url/license/developer/scm from the extension; adds `versionMapping` for both `JAVA_API` and `JAVA_RUNTIME` usages (resolves dynamic versions to concrete ones in the published POM); registers a **local-directory** Maven repository named `stagingDeploy` pointing at `build/staging-deploy` (not a remote server — this is a local staging area).
3. **Signing** (`afterEvaluate`): applies the `signing` plugin if needed; makes signing required only when the `publish` task is actually in the task graph; signs the `mavenJava` publication. Uses stock Gradle `signing` configuration — `signing.keyId`/`signing.password`/`signing.secretKeyRingFile`, or in-memory PGP keys — this plugin doesn't read GPG material itself.
4. **Staging zip**: registers `zipStagingDeploy` (a `Zip` task) that zips everything under `build/staging-deploy/**/*` into `build/tmp/staging-deploy.zip`. It depends *only* on `publishMavenJavaPublicationToStagingDeployRepository` (i.e. exactly the `mavenJava` publication published to exactly the `stagingDeploy` repo) — not on any other publication that might also target that repository.
5. **Upload**: registers `publishToMavenCentral` (a custom `PublishToMavenCentralTask`) that POSTs the zip to `https://central.sonatype.com/api/v1/publisher/upload?name=<group>:<name>:<version>&publishingType=AUTOMATIC` with `Authorization: Bearer <token>`. `publishingType=AUTOMATIC` means Central Portal auto-publishes the deployment — no manual "release" click needed on their web UI.

## Tasks

| Task | Type | Purpose |
|---|---|---|
| `publishMavenJavaPublicationToStagingDeployRepository` | `PublishToMavenRepository` (standard `maven-publish`) | Writes the signed POM + jars into `build/staging-deploy` |
| `zipStagingDeploy` | `Zip` | Zips the staging directory into `build/tmp/staging-deploy.zip` |
| `publishToMavenCentral` | `PublishToMavenCentralTask` | Uploads the zip bundle to the Central Portal |

## Central Portal token

Resolved via `resolveToken()`:

1. Project extra property named by `mavenCentralPublish.centralTokenPropertyName` (default `com.stano.maven.central.token`).
2. Falls back to environment variable **`MAVEN_TOKEN`**.

If the token can't be resolved (blank/missing), `publishToMavenCentral` fails with:

```
GradleException: No Maven Central token configured. Set the property named by mavenCentralPublish.centralTokenPropertyName, or the MAVEN_TOKEN environment variable.
```

If the staging zip doesn't exist yet (i.e. `zipStagingDeploy` hasn't run), it fails with:

```
GradleException: Staging zip not found at <path> -- run zipStagingDeploy first.
```

## Full workflow

```bash
./gradlew publishMavenJavaPublicationToStagingDeployRepository zipStagingDeploy publishToMavenCentral
# or, since publishToMavenCentral's inputs are wired lazily to zipStagingDeploy's output:
MAVEN_TOKEN=xxxxx ./gradlew publishToMavenCentral
```

## Composability with `com.stano.java-library`

When both `com.stano.java-library` and `com.stano.maven-central-publish` are applied to the same project, `java-library`'s own private-repo publication is automatically skipped — only this plugin's `mavenJava` publication exists, avoiding a duplicate-publication coordinate collision. See [`com.stano.java-library`'s composability note](java-library.md#composability-with-comstanomaven-central-publish) for details; no configuration is needed to get this behavior.

## Gotchas

- All 12 POM fields are required — there's no fallback to org-wide defaults. If you're publishing several libraries, expect to repeat this block per project (or centralize it in a `subprojects { }` block yourself).
- Blank strings fail validation the same as `null` — `pomUrl = ""` fails just like leaving it unset.
- `signing` is only *required* when `publish` is in the task graph — running `zipStagingDeploy` alone on an unsigned build won't fail for missing signing config, but the resulting artifacts won't be signed, and Central will reject unsigned uploads.
- The `stagingDeploy` repository is a **local directory**, not a remote Maven repo — don't confuse it with the `stano-maven` repository used by `com.stano.java-library`.
