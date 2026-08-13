package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.stano.gradle.base.features.BaseExtensionFeature;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class BaseExtensionFeatureTest {
  @Test
  void shouldCreateBaseExtensionFeature() {
    BaseExtensionFeature feature = new BaseExtensionFeature();
    assertNotNull(feature);
  }

  @Test
  void settingJavaVersionWithAJdkDirectoryStylePrefixShouldStripThePrefix() {
    Project project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("javaVersion", "jdk-21.0.11+10");

    new BaseExtensionFeature().apply(project);

    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals("21.0.11+10", baseExtension.getJavaVersion());
  }

  @Test
  void settingJavaVersionWithoutAPrefixShouldLeaveItUnchanged() {
    Project project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("javaVersion", "21");

    new BaseExtensionFeature().apply(project);

    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals("21", baseExtension.getJavaVersion());
  }

  @Test
  void dependencyLockingShouldDefaultToNullWhenNotExplicitlySet() {
    Project project = ProjectBuilder.builder().build();

    new BaseExtensionFeature().apply(project);

    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertNull(baseExtension.getDependencyLocking());
  }

  @Test
  void settingDependencyLockingProjectPropertyShouldOverrideTheDefault() {
    Project project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("com.stano.dependency-locking", "false");

    new BaseExtensionFeature().apply(project);

    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals(false, baseExtension.getDependencyLocking());
  }

  @Test
  void useNvmShouldDefaultToFalse() {
    Project project = ProjectBuilder.builder().build();

    new BaseExtensionFeature().apply(project);

    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals(false, baseExtension.isUseNvm());
  }

  @Test
  void settingUseNvmProjectPropertyShouldOverrideTheDefault() {
    Project project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("com.stano.use-nvm", "true");

    new BaseExtensionFeature().apply(project);

    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals(true, baseExtension.isUseNvm());
  }

  @Test
  void defaultNodeVersionShouldDefaultTo12() {
    Project project = ProjectBuilder.builder().build();

    new BaseExtensionFeature().apply(project);

    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals("12", baseExtension.getDefaultNodeVersion());
  }

  @Test
  void settingDefaultNodeVersionProjectPropertyShouldOverrideTheDefault() {
    Project project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("com.stano.default-node-version", "20.11.0");

    new BaseExtensionFeature().apply(project);

    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals("20.11.0", baseExtension.getDefaultNodeVersion());
  }
}
