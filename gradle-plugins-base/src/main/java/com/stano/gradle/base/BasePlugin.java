package com.stano.gradle.base;

import com.stano.gradle.base.features.BaseExtensionFeature;
import com.stano.gradle.base.features.JacocoAggregateFeature;
import com.stano.gradle.base.features.KotlinAnchorFeature;
import com.stano.gradle.base.features.SpotlessAnchorFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class BasePlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new BaseExtensionFeature().apply(project);
    if (project.getRootProject() == project) {
      new SpotlessAnchorFeature().apply(project);
      new KotlinAnchorFeature().apply(project);
      new JacocoAggregateFeature().apply(project);
    }
  }
}
