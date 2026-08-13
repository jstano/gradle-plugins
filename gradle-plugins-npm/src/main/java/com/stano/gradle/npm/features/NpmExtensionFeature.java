package com.stano.gradle.npm.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.npm.NpmExtension;
import org.gradle.api.Project;

public class NpmExtensionFeature implements PluginFeature {
  private static final String NPM_EXTENSION_NAME = "npm";

  @Override
  public void apply(Project project) {
    if (project.getExtensions().findByName(NPM_EXTENSION_NAME) != null) {
      return;
    }

    project.getExtensions().create(NPM_EXTENSION_NAME, NpmExtension.class, project);
  }
}
