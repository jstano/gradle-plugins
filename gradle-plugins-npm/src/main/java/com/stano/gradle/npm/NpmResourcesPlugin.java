package com.stano.gradle.npm;

import com.stano.gradle.npm.features.ConfigureNpmAssembleFeature;
import com.stano.gradle.npm.features.NpmResourcesExtensionFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

class NpmResourcesPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(NpmPlugin.class);

    new NpmResourcesExtensionFeature().apply(project);
    new ConfigureNpmAssembleFeature().apply(project);
  }
}
