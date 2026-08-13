# `com.stano.npm`

Generic npm/Node build lifecycle support. Usable on any npm project — a standalone SPA, an npm library, a lint/test-only project — with no dependency on any Java plugin being applied. For embedding an npm build's output into a Java jar's resources, layer [`com.stano.npm-resources`](npm-resources.md) on top.

Implementation class: `com.stano.gradle.npm.NpmPlugin` (package-private — reference it only via the plugin ID).

## Apply it

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.npm") version "0.1.12"
}

npm {
  nodeVersion.set("20.11.0")
}
```

## Prerequisites

- The subproject (or `npm.projectDirectory`) must contain a `package.json`.
- `npm` (or `nvm`, see below) must be resolvable on the `PATH`/environment the build runs in.
- No Java plugin is required — this plugin works standalone.

## Extension: `npm` (type `NpmExtension`)

| Property | Type | Default | Resolved from | Purpose |
|---|---|---|---|---|
| `useNvm` | `Property<Boolean>` | `root.useNvm` (see [`docs/base.md`](base.md)) | — | Build via an nvm-managed Node install instead of a system-installed npm |
| `nodeVersion` | `Property<String>` | `root.defaultNodeVersion` | — | Node version to use when `useNvm` is `true` |
| `projectDirectory` | `Property<File>` | the subproject's directory | `npmProjectPath` project/system property | Directory containing `package.json` and the npm project |
| `projectDistRoot` | `Property<File>` | the subproject's build directory | same as `projectDirectory`'s override | Root directory under which the npm build writes a `dist/` folder |
| `resourceFilesDirectory` | `Property<String>` | unset (`null`) | — | Optional extra directory watched as an `npmRunBuild` input, for projects that inject generated resource files into the npm build |
| `runNpmBuildInIde` | `Property<Boolean>` | `false` | `runNpmBuildInIde` project/system property | Whether IDE-triggered builds should still run npm steps automatically; consumed by [`com.stano.npm-resources`](npm-resources.md)'s `jar`/`test` wiring |

## Tasks

| Task | Type | Wired into | Purpose |
|---|---|---|---|
| `npmVersion` | `NpmVersionTask` | — | Runs `npm --version`, useful for diagnosing environment issues |
| `npmInstall` | `NpmInstallTask` | — | Runs `npm install`; inputs are `package.json`/`package-lock.json`, output is `node_modules/` |
| `npmClean` | `NpmCleanTask` | `clean` | Deletes the `coverage/` folder |
| `npmRunBuild` | `NpmBuildTask` | — | Runs `npm run build`; installs dependencies first if not already installed this build |
| `npmTest` | `NpmTestTask` | — | Runs `npm test` (or `npm run test:withCoverage` if that script exists in `package.json`) |

None of these tasks wire into any Java lifecycle task (`jar`, `test`, etc.) — that's [`com.stano.npm-resources`](npm-resources.md)'s job. Invoke them directly (`./gradlew npmRunBuild`), or wire them into your own tasks as needed.

## What it does under the hood

1. **Executable resolution**: if `useNvm` is `true` and either `NVM_HOME` is set or `~/.nvm/nvm.sh` exists, npm is invoked through a bundled `nvm.sh` wrapper (copied to a temp file at build time) so the configured `nodeVersion` is used. On Windows with `useNvm`, `nvm install <version>` is run first and the versioned `npm.cmd` under `%NVM_HOME%` is located directly. Otherwise, plain `npm`/`npm.cmd` is used.
2. **`NODE_OPTIONS`**: every npm invocation gets `--max_old_space_size=4096` merged into `NODE_OPTIONS`, preserving any existing value (and not duplicating the flag if it's already present).
3. **Install de-duplication**: `npmInstall`, `npmRunBuild`, and `npmTest` all funnel through the same in-process cache, so `npm install` only actually runs once per project directory per build, however many tasks touch it.
4. **Sonar wiring**: if `org.sonarqube` is already applied — directly on this project or on any ancestor project — `sonar.sources` is set to `src` (or `skipProject=true` if a `skipSonar` project property is set). This plugin never applies `org.sonarqube` itself; combine with [`com.stano.sonar`](sonar.md) for that.

## Full example

```kotlin
// build.gradle.kts — a standalone npm project, no Java involved
plugins {
  id("com.stano.npm") version "0.1.12"
  id("com.stano.sonar") version "0.1.12"
}

npm {
  useNvm.set(true)
  nodeVersion.set("20.11.0")
}
```

```bash
./gradlew npmInstall npmRunBuild npmTest
```

## Gotchas

- `useNvm`/`nodeVersion` default from the root `root { useNvm; defaultNodeVersion }` (see [`docs/base.md`](base.md)) — set them there once for a multi-module build instead of repeating them per subproject, or override per-subproject via the `npm { }` block.
- nvm resolution requires either `NVM_HOME` (Windows) or `~/.nvm/nvm.sh` (macOS/Linux) to actually exist — if neither is found, the plugin silently falls back to plain `npm` even with `useNvm=true`.
- `resourceFilesDirectory` has no default — it's only useful for projects that need an extra directory watched as a build input; leave it unset otherwise.
- Want the npm build's output packaged into a Java jar's resources, or `jar`/`test` to depend on the npm tasks? Apply [`com.stano.npm-resources`](npm-resources.md) as well.
