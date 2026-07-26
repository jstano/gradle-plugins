package com.stano.gradle.kotlin.features;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stano.gradle.java.JavaPlugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class ConfigurePluginsFeatureTest {
  @Test
  void applyingShouldApplyJavaPluginByClass() {
    var mockPlugins = mock(PluginContainer.class);
    var mockProject = mock(Project.class);
    when(mockProject.getPlugins()).thenReturn(mockPlugins);

    var feature = new ConfigurePluginsFeature();
    feature.apply(mockProject);

    verify(mockPlugins).apply(JavaPlugin.class);
  }

  @Test
  void applyingShouldApplyKotlinJvmPluginById() {
    var mockPlugins = mock(PluginContainer.class);
    var mockProject = mock(Project.class);
    when(mockProject.getPlugins()).thenReturn(mockPlugins);

    var feature = new ConfigurePluginsFeature();
    feature.apply(mockProject);

    verify(mockPlugins).apply("org.jetbrains.kotlin.jvm");
  }

  @Test
  void applyingShouldApplyJavaPluginBeforeKotlin() {
    var mockPlugins = mock(PluginContainer.class);
    var mockProject = mock(Project.class);
    when(mockProject.getPlugins()).thenReturn(mockPlugins);

    var feature = new ConfigurePluginsFeature();
    feature.apply(mockProject);

    InOrder inOrder = inOrder(mockPlugins);
    inOrder.verify(mockPlugins).apply(JavaPlugin.class);
    inOrder.verify(mockPlugins).apply("org.jetbrains.kotlin.jvm");
  }
}
