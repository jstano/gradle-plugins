package com.stano.gradle.kotlin.features;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.junit.jupiter.api.Test;

class KotlinAnchorFeatureTest {
  @Test
  void applyingShouldApplyKotlinJvmPluginToTheRootProject() {
    var mockRootPlugins = mock(PluginContainer.class);
    var mockRootProject = mock(Project.class);
    when(mockRootProject.getPlugins()).thenReturn(mockRootPlugins);

    var mockProject = mock(Project.class);
    when(mockProject.getRootProject()).thenReturn(mockRootProject);

    var feature = new KotlinAnchorFeature();
    feature.apply(mockProject);

    verify(mockRootPlugins).apply("org.jetbrains.kotlin.jvm");
  }
}
