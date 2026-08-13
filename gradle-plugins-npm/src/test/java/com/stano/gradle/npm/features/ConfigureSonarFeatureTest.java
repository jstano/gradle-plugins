package com.stano.gradle.npm.features;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.sonarqube.gradle.SonarProperties;
import org.sonarqube.gradle.SonarQubePlugin;

class ConfigureSonarFeatureTest {
  @Test
  void applyShouldNotThrowWhenSonarPluginIsNotApplied() {
    var project = ProjectBuilder.builder().build();

    assertDoesNotThrow(() -> new ConfigureSonarFeature().apply(project));
  }

  @Test
  void applyShouldNotThrowWhenSonarPluginIsAppliedOnSelf() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(SonarQubePlugin.class);

    assertDoesNotThrow(() -> new ConfigureSonarFeature().apply(project));
  }

  @Test
  void applyShouldNotThrowWhenSonarPluginIsAppliedOnAnAncestorProject() {
    var rootProject = ProjectBuilder.builder().build();
    var childProject = ProjectBuilder.builder().withParent(rootProject).build();
    rootProject.getPluginManager().apply(SonarQubePlugin.class);

    assertDoesNotThrow(() -> new ConfigureSonarFeature().apply(childProject));
  }

  @Test
  void configurePropertiesShouldSetSonarSourcesByDefault() {
    var project = ProjectBuilder.builder().build();
    Map<String, Object> map = new HashMap<>();

    new ConfigureSonarFeature().configureProperties(project, new SonarProperties(map));

    assertEquals("src", map.get("sonar.sources"));
    assertFalse(map.containsKey("skipProject"));
  }

  @Test
  void configurePropertiesShouldSetSkipProjectWhenSkipSonarPropertyIsSet() {
    var project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("skipSonar", "true");
    Map<String, Object> map = new HashMap<>();

    new ConfigureSonarFeature().configureProperties(project, new SonarProperties(map));

    assertTrue((Boolean) map.get("skipProject"));
    assertFalse(map.containsKey("sonar.sources"));
  }
}
