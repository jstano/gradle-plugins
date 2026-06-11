package com.stano.gradle.javamodule;

import com.stano.gradle.javamodule.features.ConfigureArtifactsFeature;
import com.stano.gradle.javamodule.features.ConfigureCompilersFeature;
import com.stano.gradle.javamodule.features.ConfigureDefaultDependenciesFeature;
import com.stano.gradle.javamodule.features.ConfigureJacocoFeature;
import com.stano.gradle.javamodule.features.ConfigurePluginsFeature;
import com.stano.gradle.javamodule.features.ConfigureTestPluginFeature;
import com.stano.gradle.project.ProjectPlugin;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JavaModulePlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    Project rootProject = project.getRootProject();

    // Check immediately at apply time
    boolean hasProjectPlugin = rootProject.getPlugins().stream()
        .anyMatch(p -> p instanceof ProjectPlugin);
    if (!hasProjectPlugin) {
      throw new GradleException(
          "com.stano.java-module requires com.stano.project (or com.stano.application) "
          + "to be applied to the root project.");
    }

    project.getExtensions().create("stanoJava", JavaExtension.class);

    new ConfigurePluginsFeature().apply(project);
    new ConfigureDefaultDependenciesFeature().apply(project);
    new ConfigureCompilersFeature().apply(project);
    new ConfigureTestPluginFeature().apply(project);
    new ConfigureJacocoFeature().apply(project);
    new ConfigureArtifactsFeature().apply(project);
  }
}
