package com.stano.gradle.application;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BasePlugin;
import org.gradle.api.Project;

public class ApplicationPlugin extends BasePlugin {
  @Override
  public void apply(Project project) {
    super.apply(project);
    project.getPluginManager().apply("base");
    project.getPluginManager().apply("jacoco");
    setVersion(project);
  }

  private void setVersion(Project project) {
    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    project.setVersion(new ProjectVersionProvider(project, baseExtension));
    project
        .getSubprojects()
        .forEach(
            subProject -> {
              subProject.setVersion(project.getVersion());
            });
  }
}
