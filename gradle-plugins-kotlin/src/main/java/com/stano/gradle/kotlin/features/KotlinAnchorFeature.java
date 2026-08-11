package com.stano.gradle.kotlin.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;

public class KotlinAnchorFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    project.getRootProject().getPlugins().apply("org.jetbrains.kotlin.jvm");
  }
}
