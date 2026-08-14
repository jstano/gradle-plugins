package com.stano.gradle.application;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BasePlugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Jar;

public class ApplicationPlugin extends BasePlugin {
  @Override
  public void apply(Project project) {
    super.apply(project);
    project.getPluginManager().apply("base");
    project.getPluginManager().apply("jacoco");
    setVersion(project);
    setDependencyLockingDefault(project);
    stripJarArchiveVersions(project);
  }

  /**
   * Applications aren't published as versioned Maven artifacts, so jar (and bootJar, which extends
   * Jar) output shouldn't carry a version segment either, even though {@code project.version} is
   * computed for other consumers (Docker tag, Sonar, etc. — see {@link #setVersion}). Keeps
   * build/libs filenames stable across builds.
   */
  private void stripJarArchiveVersions(Project project) {
    project
        .getAllprojects()
        .forEach(
            p ->
                p.getTasks()
                    .withType(Jar.class)
                    .configureEach(jar -> jar.getArchiveVersion().set("")));
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

  private void setDependencyLockingDefault(Project project) {
    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    if (baseExtension.getDependencyLocking() == null) {
      baseExtension.setDependencyLocking(true);
    }
  }
}
