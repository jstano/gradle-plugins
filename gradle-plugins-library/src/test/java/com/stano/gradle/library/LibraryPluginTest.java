package com.stano.gradle.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BasePluginTest;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class LibraryPluginTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheBaseExtension() {
    rootProject.getPluginManager().apply("com.stano.library");
    assertNotNull(rootProject.getExtensions().findByType(BaseExtension.class));
  }

  @Test
  void shouldDefaultDependencyLockingToFalse() {
    var project = ProjectBuilder.builder().withName("root").build();
    project.getPluginManager().apply("com.stano.library");
    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals(false, baseExtension.getDependencyLocking());
  }

  @Test
  void shouldNotOverrideAnExplicitDependencyLockingProperty() {
    var project = ProjectBuilder.builder().withName("root").build();
    project.getExtensions().getExtraProperties().set("com.stano.dependency-locking", "true");
    project.getPluginManager().apply("com.stano.library");
    BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);
    assertEquals(true, baseExtension.getDependencyLocking());
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
