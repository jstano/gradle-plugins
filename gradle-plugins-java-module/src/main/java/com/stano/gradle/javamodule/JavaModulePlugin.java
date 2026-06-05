package com.stano.gradle.javamodule;

import com.stano.gradle.javamodule.features.ConfigureArtifactsFeature;
import com.stano.gradle.javamodule.features.ConfigureCompilersFeature;
import com.stano.gradle.javamodule.features.ConfigureDefaultDependenciesFeature;
import com.stano.gradle.javamodule.features.ConfigureJacocoFeature;
import com.stano.gradle.javamodule.features.ConfigurePluginsFeature;
import com.stano.gradle.javamodule.features.ConfigureTestPluginFeature;
import com.stano.gradle.project.ProjectPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JavaModulePlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    if (!project.getRootProject().getPlugins().hasPlugin(ProjectPlugin.class)) {
      project.getRootProject().getPlugins().apply(ProjectPlugin.class);
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
