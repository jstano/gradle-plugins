package com.stano.gradle.schema.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.schema.SchemaExtension;
import com.stano.gradle.schema.SchemaPluginUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSetContainer;

public class ConfigureGenerateSqlTaskFeature implements PluginFeature {
  private static final String GROUP = "com.stano";
  private static final String ARTIFACT = "schema-sql-generator";

  @Override
  public void apply(Project project) {
    project.afterEvaluate(
        p -> {
          if (!SchemaPluginUtils.hasRuntimeDependency(p, GROUP, ARTIFACT)) {
            p.getLogger()
                .warn(
                    "com.stano.schema: {}:{} was not found on the runtimeClasspath, so the "
                        + "generateSql task was not registered. Add it (e.g. "
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
            "generateSql",
            JavaExec.class,
            task -> {
              task.setGroup("database");
              task.setDescription("Generates dialect-specific SQL DDL from schema.xml");
              task.setClasspath(
                  project
                      .getExtensions()
                      .getByType(SourceSetContainer.class)
                      .getByName("main")
                      .getRuntimeClasspath());
              task.getMainClass().set("com.stano.schema.gensql.GenSQL");

              File schemaFile = extension.getSchemaFile().get().getAsFile();
              Set<String> databaseTypes = extension.getDatabaseTypes().get();
              task.args(buildArgs(extension, schemaFile, databaseTypes));

              task.getInputs().file(extension.getSchemaFile());
              String baseName = SchemaPluginUtils.baseName(schemaFile);
              databaseTypes.forEach(
                  databaseType ->
                      task.getOutputs()
                          .file(
                              new File(
                                  schemaFile.getParentFile(),
                                  baseName + "-" + databaseType.toLowerCase() + ".sql")));
            });
  }

  private List<String> buildArgs(
      SchemaExtension extension, File schemaFile, Set<String> databaseTypes) {
    List<String> args = new ArrayList<>();
    args.add(String.join(",", databaseTypes));
    args.add(schemaFile.getAbsolutePath());

    if (extension.getForeignKeyMode().isPresent()) {
      args.add("--foreign-key-mode=" + extension.getForeignKeyMode().get());
    }
    if (extension.getBooleanMode().isPresent()) {
      args.add("--boolean-mode=" + extension.getBooleanMode().get());
    }
    if (extension.getOutputMode().isPresent()) {
      String outputMode = extension.getOutputMode().get();
      if ("INDEXES_ONLY".equals(outputMode)) {
        args.add("--output-indexes-only");
      } else if ("TRIGGERS_ONLY".equals(outputMode)) {
        args.add("--output-triggers-only");
      }
    }
    if (extension.getPostgresqlVersion().isPresent()) {
      args.add("--postgresql-version=" + extension.getPostgresqlVersion().get());
    }

    return args;
  }
}
