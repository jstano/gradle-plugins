package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
