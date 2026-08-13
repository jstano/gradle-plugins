package com.stano.gradle.npm;

import com.stano.gradle.npm.features.ConfigureSonarFeature;
import com.stano.gradle.npm.features.ConfigureTasksFeature;
import com.stano.gradle.npm.features.NpmExtensionFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

class NpmPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new NpmExtensionFeature().apply(project);
    new ConfigureSonarFeature().apply(project);
    new ConfigureTasksFeature().apply(project);
  }
}
