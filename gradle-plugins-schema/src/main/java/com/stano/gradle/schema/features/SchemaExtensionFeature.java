package com.stano.gradle.schema.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.schema.SchemaExtension;
import org.gradle.api.Project;

public class SchemaExtensionFeature implements PluginFeature {
  private static final String SCHEMA_EXTENSION_NAME = "schema";

  @Override
  public void apply(Project project) {
    if (project.getExtensions().findByName(SCHEMA_EXTENSION_NAME) != null) {
      return;
    }

    project.getExtensions().create(SCHEMA_EXTENSION_NAME, SchemaExtension.class, project);
  }
}
