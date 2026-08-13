package com.stano.gradle.schema.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.schema.SchemaExtension;
import com.stano.gradle.schema.SchemaPluginUtils;
import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSetContainer;

public class ConfigureGenerateSchemaDiagramTaskFeature implements PluginFeature {
  private static final String GROUP = "com.stano";
  private static final String ARTIFACT = "schema-diagram-generator";

  @Override
  public void apply(Project project) {
    project.afterEvaluate(
        p -> {
          if (!SchemaPluginUtils.hasRuntimeDependency(p, GROUP, ARTIFACT)) {
            p.getLogger()
                .warn(
                    "com.stano.schema: {}:{} was not found on the runtimeClasspath, so the "
                        + "generateSchemaDiagram task was not registered. Add it (e.g. "
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
            "generateSchemaDiagram",
            JavaExec.class,
            task -> {
              task.setGroup("documentation");
              task.setDescription("Generates an ER diagram (Mermaid or PlantUML) from schema.xml");
              task.setClasspath(
                  project
                      .getExtensions()
                      .getByType(SourceSetContainer.class)
                      .getByName("main")
                      .getRuntimeClasspath());
              task.getMainClass().set("com.stano.schema.gendiagram.GenDiagram");

              File schemaFile = extension.getSchemaFile().get().getAsFile();
              String diagramFormat = extension.getDiagramFormat().get();
              task.args(diagramFormat, schemaFile.getAbsolutePath());

              task.getInputs().file(extension.getSchemaFile());
              String outputExtension = "PLANTUML".equals(diagramFormat) ? "puml" : "mmd";
              task.getOutputs()
                  .file(
                      new File(
                          schemaFile.getParentFile(),
                          SchemaPluginUtils.baseName(schemaFile) + "." + outputExtension));
            });
  }
}
