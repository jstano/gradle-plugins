package com.stano.gradle.sonar;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class SonarPluginTest {
  @Test
  void shouldNotApplyTheSonarQubePluginWhenHostAndTokenAreNotConfigured() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.stano.sonar");
    assertFalse(project.getPluginManager().hasPlugin("org.sonarqube"));
  }

  @Test
  void shouldApplyTheSonarQubePluginWhenHostAndTokenAreConfigured() {
    var project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("sonar.host.url", "https://sonar.example.com");
    project.getExtensions().getExtraProperties().set("sonar.token", "test-token");

    project.getPluginManager().apply("com.stano.sonar");

    assertTrue(project.getPluginManager().hasPlugin("org.sonarqube"));
  }
}
