package com.stano.gradle.sonar;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class SonarPluginTest {
  @Test
  void shouldApplyTheSonarQubePlugin() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.stano.sonar");
    assertTrue(project.getPluginManager().hasPlugin("org.sonarqube"));
  }
}
