package com.stano.gradle.project;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.stano.gradle.RootExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class ProjectPluginTest {
  @Test
  void shouldAddTheRootExtension() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.stano.project");
    assertNotNull(project.getExtensions().findByType(RootExtension.class));
  }
}
