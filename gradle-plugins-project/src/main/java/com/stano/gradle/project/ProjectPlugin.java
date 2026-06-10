package com.stano.gradle.project;

import com.stano.gradle.RootExtensionFeature;
import com.stano.gradle.project.features.JacocoAggregateFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ProjectPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new RootExtensionFeature().apply(project);

    if (project.getRootProject() == project) {
      new JacocoAggregateFeature().apply(project);
    }
  }
}
