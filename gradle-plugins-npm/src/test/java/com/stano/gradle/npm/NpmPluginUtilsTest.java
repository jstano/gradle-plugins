package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.features.NpmExtensionFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class NpmPluginUtilsTest extends BasePluginTest {
  @AfterEach
  void clearIdeSystemProperty() {
    System.clearProperty("idea.active");
  }

  @Test
  void shouldExecuteShouldReturnTrueWhenNotRunningInsideIde() {
    System.clearProperty("idea.active");
    new NpmExtensionFeature().apply(childProject);

    assertTrue(NpmPluginUtils.shouldExecute(childProject));
  }

  @Test
  void shouldExecuteShouldReturnFalseWhenRunningInsideIdeAndRunNpmBuildInIdeIsNotSet() {
    System.setProperty("idea.active", "true");
    new NpmExtensionFeature().apply(childProject);

    assertFalse(NpmPluginUtils.shouldExecute(childProject));
  }

  @Test
  void shouldExecuteShouldReturnTrueWhenRunningInsideIdeAndRunNpmBuildInIdeIsSet() {
    childProject.getExtensions().getExtraProperties().set("runNpmBuildInIde", "true");
    System.setProperty("idea.active", "true");
    new NpmExtensionFeature().apply(childProject);

    assertTrue(NpmPluginUtils.shouldExecute(childProject));
  }
}
