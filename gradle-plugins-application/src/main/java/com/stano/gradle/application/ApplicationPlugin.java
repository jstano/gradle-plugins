package com.stano.gradle.application;

import com.stano.gradle.ProjectVersionProvider;
import com.stano.gradle.RootExtension;
import com.stano.gradle.project.ProjectPlugin;
import org.gradle.api.Project;

public class ApplicationPlugin extends ProjectPlugin {
  @Override
  public void apply(Project project) {
    super.apply(project);
    project.getPluginManager().apply("base");
    project.getPluginManager().apply("jacoco");
    setVersion(project);
  }

  private void setVersion(Project project) {
    RootExtension rootExtension = project.getExtensions().getByType(RootExtension.class);
    project.setVersion(new ProjectVersionProvider(project, rootExtension));
    project
        .getSubprojects()
        .forEach(
            subProject -> {
              subProject.setVersion(project.getVersion());
            });
  }
}
