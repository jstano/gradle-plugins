package com.stano.gradle.application;

import com.stano.gradle.ProjectVersionProvider;
import com.stano.gradle.RootExtension;
import com.stano.gradle.project.ProjectPlugin;
import org.gradle.api.Project;

public class ApplicationPlugin extends ProjectPlugin {
  @Override
  public void apply(Project project) {
    super.apply(project);

    overrideVersion(project);

    project.getSubprojects().forEach(subProject -> {
      subProject.setVersion(project.getVersion());
    });
  }

  private void overrideVersion(Project project) {
    RootExtension rootExtension = project.getExtensions().getByType(RootExtension.class);

    if (!rootExtension.isUseSemVer()) {
      project.setVersion(new ProjectVersionProvider(project, rootExtension));
    }
  }
}
