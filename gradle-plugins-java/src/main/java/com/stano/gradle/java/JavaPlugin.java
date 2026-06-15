package com.stano.gradle.java;

import com.stano.gradle.base.BasePlugin;
import com.stano.gradle.java.features.ConfigureArtifactsFeature;
import com.stano.gradle.java.features.ConfigureCompilersFeature;
import com.stano.gradle.java.features.ConfigureDefaultDependenciesFeature;
import com.stano.gradle.java.features.ConfigureJacocoFeature;
import com.stano.gradle.java.features.ConfigurePluginsFeature;
import com.stano.gradle.java.features.ConfigureSpotlessFeature;
import com.stano.gradle.java.features.ConfigureTestPluginFeature;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JavaPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    Project rootProject = project.getRootProject();
    // Check immediately at apply time
    boolean hasProjectPlugin =
        rootProject.getPlugins().stream().anyMatch(p -> p instanceof BasePlugin);
    if (!hasProjectPlugin) {
      throw new GradleException(
          "com.stano.java requires com.stano.base (or com.stano.application) "
              + "to be applied to the root project.");
    }
    project.getExtensions().create("javaConventions", JavaExtension.class);
    new ConfigurePluginsFeature().apply(project);
    new ConfigureSpotlessFeature().apply(project);
    new ConfigureDefaultDependenciesFeature().apply(project);
    new ConfigureCompilersFeature().apply(project);
    new ConfigureTestPluginFeature().apply(project);
    new ConfigureJacocoFeature().apply(project);
    new ConfigureArtifactsFeature().apply(project);
  }
}
