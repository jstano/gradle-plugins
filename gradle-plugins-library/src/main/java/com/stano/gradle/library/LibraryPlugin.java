package com.stano.gradle.library;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BasePlugin;
import org.gradle.api.Project;

public class LibraryPlugin extends BasePlugin {
  @Override
  public void apply(Project project) {
    super.apply(project);
    project.getPluginManager().apply("base");
    project.getPluginManager().apply("jacoco");
    setDependencyLockingDefault(project);
  }

  private void setDependencyLockingDefault(Project project) {
    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    if (baseExtension.getDependencyLocking() == null) {
      baseExtension.setDependencyLocking(false);
    }
  }
}
