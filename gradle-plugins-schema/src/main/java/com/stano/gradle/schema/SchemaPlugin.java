package com.stano.gradle.schema;

import com.stano.gradle.schema.features.ConfigureGenerateSchemaDiagramTaskFeature;
import com.stano.gradle.schema.features.ConfigureGenerateSqlTaskFeature;
import com.stano.gradle.schema.features.ConfigureInstallSchemaTaskFeature;
import com.stano.gradle.schema.features.SchemaExtensionFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SchemaPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new SchemaExtensionFeature().apply(project);
    new ConfigureGenerateSchemaDiagramTaskFeature().apply(project);
    new ConfigureGenerateSqlTaskFeature().apply(project);
    new ConfigureInstallSchemaTaskFeature().apply(project);
  }
}
