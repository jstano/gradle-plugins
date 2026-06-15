package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class BasePluginTest {
  @Test
  void shouldAddTheBaseExtension() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.stano.base");
    assertNotNull(project.getExtensions().findByType(BaseExtension.class));
  }
}
