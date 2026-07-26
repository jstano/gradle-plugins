package com.stano.gradle.kotlin;

import com.stano.gradle.kotlin.features.ConfigureCompilersFeature;
import com.stano.gradle.kotlin.features.ConfigurePluginsFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class KotlinPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new ConfigurePluginsFeature().apply(project);
    new ConfigureCompilersFeature().apply(project);
  }
}
