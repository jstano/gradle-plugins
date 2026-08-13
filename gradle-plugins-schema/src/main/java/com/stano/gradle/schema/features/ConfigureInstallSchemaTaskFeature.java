package com.stano.gradle.schema.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.schema.SchemaExtension;
import com.stano.gradle.schema.SchemaPluginUtils;
import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSetContainer;

public class ConfigureInstallSchemaTaskFeature implements PluginFeature {
  private static final String GROUP = "com.stano";
  private static final String ARTIFACT = "schema-installer-flyway";

  @Override
  public void apply(Project project) {
    project.afterEvaluate(
        p -> {
          if (!SchemaPluginUtils.hasRuntimeDependency(p, GROUP, ARTIFACT)) {
            p.getLogger()
                .warn(
                    "com.stano.schema: {}:{} was not found on the runtimeClasspath, so the "
                        + "installSchema task was not registered. Add it (e.g. "
                        + "implementation(\"{}:{}:<version>\")) to enable it.",
                    GROUP,
                    ARTIFACT,
                    GROUP,
                    ARTIFACT);
            return;
          }

          registerTask(p, p.getExtensions().getByType(SchemaExtension.class));
        });
  }

  private void registerTask(Project project, SchemaExtension extension) {
    project
        .getTasks()
        .register(
            "installSchema",
            JavaExec.class,
            task -> {
              task.setGroup("database");
              task.setDescription(
                  "Installs (or migrates, if already installed) the schema.xml definition into "
                      + "a live database via Flyway. Never runs as part of build/check; invoke "
                      + "it explicitly.");
              task.setClasspath(
                  project
                      .getExtensions()
                      .getByType(SourceSetContainer.class)
                      .getByName("main")
                      .getRuntimeClasspath());
              task.getMainClass().set("com.stano.schema.installer.flyway.InstallSchema");

              File schemaFile = extension.getSchemaFile().get().getAsFile();
              task.args(schemaFile.getAbsolutePath());
              if (extension.getMigrationScriptLocator().isPresent()) {
                task.args(extension.getMigrationScriptLocator().get());
              }

              // Connection credentials are passed as environment variables, not command-line
              // arguments, so they don't leak into process listings (`ps`).
              if (extension.getSchemaJdbcUrl().isPresent()) {
                task.environment("SCHEMA_JDBC_URL", extension.getSchemaJdbcUrl().get());
              }
              if (extension.getSchemaJdbcUsername().isPresent()) {
                task.environment("SCHEMA_JDBC_USERNAME", extension.getSchemaJdbcUsername().get());
              }
              if (extension.getSchemaJdbcPassword().isPresent()) {
                task.environment("SCHEMA_JDBC_PASSWORD", extension.getSchemaJdbcPassword().get());
              }

              task.getInputs().file(extension.getSchemaFile());
            });
  }
}
