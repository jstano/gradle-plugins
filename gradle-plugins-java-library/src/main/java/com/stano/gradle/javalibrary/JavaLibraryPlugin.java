package com.stano.gradle.javalibrary;

import com.stano.gradle.javalibrary.features.ConfigureArtifactsFeature;
import com.stano.gradle.javalibrary.features.ConfigureJavadocFeature;
import com.stano.gradle.javalibrary.features.ConfigurePluginsFeature;
import com.stano.gradle.javalibrary.features.ConfigurePublishFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JavaLibraryPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new ConfigurePluginsFeature().apply(project);
    new ConfigureArtifactsFeature().apply(project);
    new ConfigurePublishFeature().apply(project);
    new ConfigureJavadocFeature().apply(project);
  }
}
