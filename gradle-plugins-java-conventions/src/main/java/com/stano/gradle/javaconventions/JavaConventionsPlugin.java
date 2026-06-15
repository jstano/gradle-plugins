package com.stano.gradle.javaconventions;

import com.stano.gradle.base.ProjectPlugin;
import com.stano.gradle.javaconventions.features.ConfigureArtifactsFeature;
import com.stano.gradle.javaconventions.features.ConfigureCompilersFeature;
import com.stano.gradle.javaconventions.features.ConfigureDefaultDependenciesFeature;
import com.stano.gradle.javaconventions.features.ConfigureJacocoFeature;
import com.stano.gradle.javaconventions.features.ConfigurePluginsFeature;
import com.stano.gradle.javaconventions.features.ConfigureSpotlessFeature;
import com.stano.gradle.javaconventions.features.ConfigureTestPluginFeature;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JavaConventionsPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    Project rootProject = project.getRootProject();
    // Check immediately at apply time
    boolean hasProjectPlugin =
        rootProject.getPlugins().stream().anyMatch(p -> p instanceof ProjectPlugin);
    if (!hasProjectPlugin) {
      throw new GradleException(
          "com.stano.java-conventions requires com.stano.base (or com.stano.application) "
              + "to be applied to the root project.");
    }
    project.getExtensions().create("javaConventions", JavaConventionsExtension.class);
    new ConfigurePluginsFeature().apply(project);
    new ConfigureSpotlessFeature().apply(project);
    new ConfigureDefaultDependenciesFeature().apply(project);
    new ConfigureCompilersFeature().apply(project);
    new ConfigureTestPluginFeature().apply(project);
    new ConfigureJacocoFeature().apply(project);
    new ConfigureArtifactsFeature().apply(project);
  }
}
