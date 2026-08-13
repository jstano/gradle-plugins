package com.stano.gradle.npm.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.NpmExtension;
import org.junit.jupiter.api.Test;

class NpmExtensionFeatureTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheNpmExtension() {
    new NpmExtensionFeature().apply(childProject);

    assertNotNull(childProject.getExtensions().findByType(NpmExtension.class));
  }

  @Test
  void applyingThePluginTwiceShouldBeIdempotent() {
    new NpmExtensionFeature().apply(childProject);
    NpmExtension firstExtension = childProject.getExtensions().getByType(NpmExtension.class);

    new NpmExtensionFeature().apply(childProject);
    NpmExtension secondExtension = childProject.getExtensions().getByType(NpmExtension.class);

    assertSame(firstExtension, secondExtension);
  }

  @Test
  void useNvmShouldDefaultFromRootUseNvm() {
    BaseExtension baseExtension = rootProject.getExtensions().getByType(BaseExtension.class);
    baseExtension.setUseNvm(true);

    new NpmExtensionFeature().apply(childProject);

    NpmExtension npmExtension = childProject.getExtensions().getByType(NpmExtension.class);
    assertTrue(npmExtension.getUseNvm().get());
  }

  @Test
  void nodeVersionShouldDefaultFromRootDefaultNodeVersion() {
    BaseExtension baseExtension = rootProject.getExtensions().getByType(BaseExtension.class);
    baseExtension.setDefaultNodeVersion("20.11.0");

    new NpmExtensionFeature().apply(childProject);

    NpmExtension npmExtension = childProject.getExtensions().getByType(NpmExtension.class);
    assertEquals("20.11.0", npmExtension.getNodeVersion().get());
  }

  @Test
  void resourceFilesDirectoryShouldHaveNoDefault() {
    new NpmExtensionFeature().apply(childProject);

    NpmExtension npmExtension = childProject.getExtensions().getByType(NpmExtension.class);
    assertNull(npmExtension.getResourceFilesDirectory().getOrNull());
  }

  @Test
  void projectDirectoryShouldDefaultToTheProjectDirectory() {
    new NpmExtensionFeature().apply(childProject);

    NpmExtension npmExtension = childProject.getExtensions().getByType(NpmExtension.class);
    assertEquals(childProject.getProjectDir(), npmExtension.getProjectDirectory().get());
  }
}
