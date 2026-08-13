# `com.stano.schema`

Adds `generateSchemaDiagram`, `generateSql`, and `installSchema` tasks that run `com.stano.schema.gendiagram.GenDiagram`, `com.stano.schema.gensql.GenSQL`, and `com.stano.schema.installer.flyway.InstallSchema` against a `schema.xml` definition — producing an ER diagram (Mermaid or PlantUML), dialect-specific SQL DDL (H2/PostgreSQL/SQL Server), and installing/migrating the schema into a live database via Flyway, respectively. Each task is wired up only when the project itself already declares the matching dependency (`com.stano:schema-diagram-generator` / `com.stano:schema-sql-generator` / `com.stano:schema-installer-flyway`) — this plugin never adds any of these dependencies or pins a version for you.

Implementation class: `com.stano.gradle.schema.SchemaPlugin`.

## Apply it

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.java")
  id("com.stano.schema") version "0.1.12"
}

dependencies {
  implementation("com.stano:schema-diagram-generator:0.54.0")
  implementation("com.stano:schema-sql-generator:0.54.0")
  implementation("com.stano:schema-installer-flyway:0.54.0")
}
```

## Prerequisites

- The `java` plugin (directly, or via [`com.stano.java`](java.md)) must be applied — all three tasks run via `JavaExec` off `sourceSets.main.runtimeClasspath`.
- `implementation("com.stano:schema-diagram-generator:<version>")` must be declared for `generateSchemaDiagram` to be registered.
- `implementation("com.stano:schema-sql-generator:<version>")` must be declared for `generateSql` to be registered.
- `implementation("com.stano:schema-installer-flyway:<version>")` (plus a JDBC driver for your target database) must be declared for `installSchema` to be registered.
- A `schema.xml` file at `schema.schemaFile` (default `src/main/resources/db/schema.xml`).

## Extension: `schema` (type `SchemaExtension`)

| Property | Type | Default | Purpose |
|---|---|---|---|
| `schemaFile` | `RegularFileProperty` | `src/main/resources/db/schema.xml` | The schema definition all three tasks read |
| `diagramFormat` | `Property<String>` | `diagramFormat` project/system property, else `"MERMAID"` | `MERMAID` or `PLANTUML` — passed to `GenDiagram` and determines the `.mmd`/`.puml` output extension |
| `databaseTypes` | `SetProperty<String>` | `["POSTGRESQL"]` | Target dialects for `GenSQL` — any of `H2`, `POSTGRESQL`, `SQL_SERVER`; one output `.sql` file is produced per entry |
| `foreignKeyMode` | `Property<String>` | unset | Passed to `GenSQL` as `--foreign-key-mode=`; when unset, `GenSQL` uses the mode declared in `schema.xml` itself |
| `booleanMode` | `Property<String>` | unset | Passed to `GenSQL` as `--boolean-mode=`; when unset, `GenSQL` uses the mode declared in `schema.xml` itself |
| `outputMode` | `Property<String>` | unset | `INDEXES_ONLY` or `TRIGGERS_ONLY` — passed to `GenSQL` as `--output-indexes-only`/`--output-triggers-only`; any other value (or unset) generates everything |
| `postgresqlVersion` | `Property<Integer>` | unset | Passed to `GenSQL` as `--postgresql-version=` when set |
| `migrationScriptLocator` | `Property<String>` | unset | Passed to `InstallSchema` as the Flyway migration script location; when unset, `FlywaySchemaInstaller` falls back to `db/migration` on the classpath |
| `schemaJdbcUrl` | `Property<String>` | `schemaJdbcUrl` project/system property, else `SCHEMA_JDBC_URL` env var | JDBC URL `installSchema` connects to |
| `schemaJdbcUsername` | `Property<String>` | `schemaJdbcUsername` project/system property, else `SCHEMA_JDBC_USERNAME` env var | Username `installSchema` connects with |
| `schemaJdbcPassword` | `Property<String>` | `schemaJdbcPassword` project/system property, else `SCHEMA_JDBC_PASSWORD` env var | Password `installSchema` connects with |

## Tasks

| Task | Type | Group | Registered when | Purpose |
|---|---|---|---|---|
| `generateSchemaDiagram` | `JavaExec` | `documentation` | `com.stano:schema-diagram-generator` is on `runtimeClasspath` | Runs `GenDiagram <diagramFormat> <schemaFile>`, producing `schema.mmd`/`schema.puml` next to `schema.xml` |
| `generateSql` | `JavaExec` | `database` | `com.stano:schema-sql-generator` is on `runtimeClasspath` | Runs `GenSQL <databaseTypes> <schemaFile> [flags]`, producing one `schema-<databasetype>.sql` file per entry in `databaseTypes` |
| `installSchema` | `JavaExec` | `database` | `com.stano:schema-installer-flyway` is on `runtimeClasspath` | Runs `InstallSchema <schemaFile> [migrationScriptLocator]` — installs the schema into the database at `schemaJdbcUrl` if not already installed, otherwise runs pending Flyway migrations. **Never wired into `build`/`check`/`test` — invoke it explicitly.** |

## What it does under the hood

1. **Dependency detection, not injection**: in `afterEvaluate` (so the project's own `dependencies { }` block has already run), each feature resolves `runtimeClasspath` and checks whether it contains an artifact matching its generator/installer dependency. This plugin deliberately never adds any of these dependencies itself, so you control the exact tool version via your own dependency declaration (or a BOM).
2. **Missing dependency ⇒ warning, not failure**: if the corresponding dependency isn't found (or the `java` plugin isn't applied at all, so `runtimeClasspath` doesn't exist), a warning is logged and that task is simply not registered — the rest of the build is unaffected.
3. **Classpath**: all three tasks run with `classpath = sourceSets.main.get().runtimeClasspath`, exactly like a hand-rolled `JavaExec` task would.
4. **Inputs/outputs**: `generateSchemaDiagram`/`generateSql` declare `schemaFile` as an input and their generated file(s) as outputs, so they participate normally in up-to-date checking and the build cache. `installSchema` declares `schemaFile` as an input but **no outputs** — it mutates a live database, not a file, so it's never considered up-to-date and always re-runs when invoked.
5. **Credentials via environment, not arguments**: `installSchema` passes `schemaJdbcUrl`/`schemaJdbcUsername`/`schemaJdbcPassword` to the child process as the `SCHEMA_JDBC_URL`/`SCHEMA_JDBC_USERNAME`/`SCHEMA_JDBC_PASSWORD` environment variables, not command-line arguments, so they don't leak into process listings (`ps`). If a credential property is unset, its environment variable is simply omitted — `InstallSchema` then fails fast with a clear "environment variable not set" error rather than connecting with a blank value.

## Full example

```kotlin
// build.gradle.kts
plugins {
  id("com.stano.java")
  id("com.stano.spring-boot")
  id("com.stano.schema") version "0.1.12"
}

dependencies {
  implementation("com.stano:msp-schema-starter")
  implementation("com.stano:schema-diagram-generator:0.54.0")
  implementation("com.stano:schema-sql-generator:0.54.0")
  implementation("com.stano:schema-installer-flyway:0.54.0")
  runtimeOnly("org.postgresql:postgresql")
}

schema {
  databaseTypes.set(setOf("POSTGRESQL", "H2"))
}
```

```bash
./gradlew generateSchemaDiagram generateSql
./gradlew generateSchemaDiagram -PdiagramFormat=PLANTUML

# installSchema reads credentials from env vars (or -P project properties / schema { } block)
SCHEMA_JDBC_URL=jdbc:postgresql://localhost/mydb \
SCHEMA_JDBC_USERNAME=myuser \
SCHEMA_JDBC_PASSWORD=mypassword \
  ./gradlew installSchema
```

## Gotchas

- Forgetting to declare `implementation("com.stano:schema-diagram-generator:...")` (or `schema-sql-generator`/`schema-installer-flyway`) doesn't fail the build — check the build log for the `com.stano.schema: ... was not found on the runtimeClasspath` warning if a task you expected isn't there.
- `databaseTypes`, `foreignKeyMode`, `booleanMode`, and `outputMode` only affect `generateSql`; `migrationScriptLocator`/`schemaJdbcUrl`/`schemaJdbcUsername`/`schemaJdbcPassword` only affect `installSchema`; `generateSchemaDiagram` only reads `schemaFile`/`diagramFormat`.
- `foreignKeyMode`/`booleanMode` left unset isn't the same as passing a value — `GenSQL` falls back to whatever `schema.xml`'s own attributes declare, so only set these when you need to override the schema file.
- `installSchema` mutates a real database — it's deliberately never wired into `build`/`check`/`test`, and there's no built-in safeguard against pointing `schemaJdbcUrl` at the wrong environment. Treat it like any other destructive ops command.
- `schema-installer-flyway` doesn't bundle JDBC drivers (except H2, for its own tests) — add `runtimeOnly("org.postgresql:postgresql")` or the SQL Server equivalent yourself.
