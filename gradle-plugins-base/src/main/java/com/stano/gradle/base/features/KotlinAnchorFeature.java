package com.stano.gradle.base.features;

import org.gradle.api.Project;

public class KotlinAnchorFeature {
  public void apply(Project project) {
    project.getPlugins().apply("org.jetbrains.kotlin.jvm");
  }
}
