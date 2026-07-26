package com.stano.gradle.sonar.features;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class ConfigureSonarPropertiesFeatureTest {
  @Test
  void buildPropertiesShouldSetHostTokenAndProjectMetadata() {
    var project = ProjectBuilder.builder().withName("my-module").build();
    project.setGroup("com.example");
    project.setVersion("1.2.3");

    var properties =
        new ConfigureSonarPropertiesFeature()
            .buildProperties(project, "https://sonar.example.com", "test-token");

    assertEquals("https://sonar.example.com", properties.get("sonar.host.url"));
    assertEquals("test-token", properties.get("sonar.token"));
    assertEquals("my-module", properties.get("sonar.projectName"));
    assertEquals("com.example:my-module", properties.get("sonar.projectKey"));
    assertEquals("1.2.3", properties.get("sonar.projectVersion"));
  }
}
