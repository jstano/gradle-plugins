package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.diffplug.gradle.spotless.SpotlessPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class BasePluginTest {
  @Test
  void shouldAddTheBaseExtension() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.stano.base");
    assertNotNull(project.getExtensions().findByType(BaseExtension.class));
  }

  @Test
  void shouldApplySpotlessToAnchorTheRootClassloader() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.stano.base");
    assertNotNull(project.getPlugins().findPlugin(SpotlessPlugin.class));
  }

  @Test
  void shouldApplyKotlinToAnchorTheRootClassloader() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.stano.base");
    assertNotNull(project.getPlugins().findPlugin("org.jetbrains.kotlin.jvm"));
  }
}
