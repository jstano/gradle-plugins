package com.stano.gradle.library;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BasePluginTest;
import org.junit.jupiter.api.Test;

class LibraryPluginTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheBaseExtension() {
    rootProject.getPluginManager().apply("com.stano.library");
    assertNotNull(rootProject.getExtensions().findByType(BaseExtension.class));
  }

  @Test
  void applyingThePluginShouldRegisterTheJacocoRootReportTask() {
    rootProject.getPluginManager().apply("com.stano.library");
    assertNotNull(rootProject.getTasks().findByName("jacocoRootReport"));
  }

  @Test
  void applyingThePluginShouldApplyTheBasePlugin() {
    rootProject.getPluginManager().apply("com.stano.library");
    assertTrue(rootProject.getPlugins().hasPlugin("base"));
  }

  @Test
  void applyingThePluginShouldApplyTheJacocoPlugin() {
    rootProject.getPluginManager().apply("com.stano.library");
    assertTrue(rootProject.getPlugins().hasPlugin("jacoco"));
  }
}
