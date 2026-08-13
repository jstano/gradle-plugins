package com.stano.gradle.npm.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.npm.NpmResourcesExtension;
import org.gradle.api.Project;

public class NpmResourcesExtensionFeature implements PluginFeature {
  private static final String NPM_RESOURCES_EXTENSION_NAME = "npmResources";

  @Override
  public void apply(Project project) {
    if (project.getExtensions().findByName(NPM_RESOURCES_EXTENSION_NAME) != null) {
      return;
    }

    project
        .getExtensions()
        .create(NPM_RESOURCES_EXTENSION_NAME, NpmResourcesExtension.class, project);
  }
}
