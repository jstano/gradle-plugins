package com.stano.gradle.application;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.stano.gradle.base.BaseExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class ApplicationPluginTest {
  @Test
  void shouldCreateBaseExtensionAndSetDynamicVersion() {
    var rootProject = ProjectBuilder.builder().withName("root").build();
    var childProject = ProjectBuilder.builder().withName("child").withParent(rootProject).build();
    rootProject.getPluginManager().apply("com.stano.application");
    assertNotNull(rootProject.getExtensions().findByType(BaseExtension.class));
    assertInstanceOf(ProjectVersionProvider.class, rootProject.getVersion());
    assertInstanceOf(ProjectVersionProvider.class, childProject.getVersion());
  }
}
