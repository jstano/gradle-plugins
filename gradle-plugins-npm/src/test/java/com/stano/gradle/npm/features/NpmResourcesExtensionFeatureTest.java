package com.stano.gradle.npm.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.NpmResourcesExtension;
import org.junit.jupiter.api.Test;

class NpmResourcesExtensionFeatureTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheNpmResourcesExtension() {
    new NpmResourcesExtensionFeature().apply(childProject);

    assertNotNull(childProject.getExtensions().findByType(NpmResourcesExtension.class));
  }

  @Test
  void applyingThePluginTwiceShouldBeIdempotent() {
    new NpmResourcesExtensionFeature().apply(childProject);
    NpmResourcesExtension firstExtension =
        childProject.getExtensions().getByType(NpmResourcesExtension.class);

    new NpmResourcesExtensionFeature().apply(childProject);
    NpmResourcesExtension secondExtension =
        childProject.getExtensions().getByType(NpmResourcesExtension.class);

    assertSame(firstExtension, secondExtension);
  }

  @Test
  void assembleOutputPathShouldDefaultFromRootContextName() {
    BaseExtension baseExtension = rootProject.getExtensions().getByType(BaseExtension.class);
    baseExtension.setContextName("my-context");

    new NpmResourcesExtensionFeature().apply(childProject);

    NpmResourcesExtension npmResourcesExtension =
        childProject.getExtensions().getByType(NpmResourcesExtension.class);
    assertEquals("my-context", npmResourcesExtension.getAssembleOutputPath().get());
  }

  @Test
  void assembleOutputPathShouldPreferTheNpmOutputPathProjectProperty() {
    childProject.getExtensions().getExtraProperties().set("npmOutputPath", "custom-output");

    new NpmResourcesExtensionFeature().apply(childProject);

    NpmResourcesExtension npmResourcesExtension =
        childProject.getExtensions().getByType(NpmResourcesExtension.class);
    assertEquals("custom-output", npmResourcesExtension.getAssembleOutputPath().get());
  }

  @Test
  void resourceOutputPathShouldDefaultToEmpty() {
    new NpmResourcesExtensionFeature().apply(childProject);

    NpmResourcesExtension npmResourcesExtension =
        childProject.getExtensions().getByType(NpmResourcesExtension.class);
    assertEquals("", npmResourcesExtension.getResourceOutputPath().get());
  }

  @Test
  void resourceOutputPathShouldPreferTheNpmResourceOutputPathProjectProperty() {
    childProject.getExtensions().getExtraProperties().set("npmResourceOutputPath", "custom/output");

    new NpmResourcesExtensionFeature().apply(childProject);

    NpmResourcesExtension npmResourcesExtension =
        childProject.getExtensions().getByType(NpmResourcesExtension.class);
    assertEquals("custom/output", npmResourcesExtension.getResourceOutputPath().get());
  }
}
