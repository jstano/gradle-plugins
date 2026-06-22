package com.stano.gradle.base.features;

import com.diffplug.gradle.spotless.SpotlessPlugin;
import org.gradle.api.Project;

public class SpotlessAnchorFeature {
  public void apply(Project project) {
    project.getPlugins().apply(SpotlessPlugin.class);
  }
}
