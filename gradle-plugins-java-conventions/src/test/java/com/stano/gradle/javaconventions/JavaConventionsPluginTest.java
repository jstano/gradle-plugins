package com.stano.gradle.javaconventions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class JavaConventionsPluginTest extends BasePluginTest {
  @BeforeEach
  void setup() {
    System.setProperty("stanoMavenUrl", "https://maven.stano.com");
    System.setProperty("stanoMavenUsername", "MAVEN_USERNAME");
    System.setProperty("stanoMavenPassword", "MAVEN_PASSWORD");
  }

  @Test
  void shouldApplyStanoProjectPluginIfNotAlreadyApplied() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(rootProject.getPluginManager().hasPlugin("stano-project"));
  }

  @Test
  void shouldVerifyThatCommonPluginsAreApplied() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(childProject.getPluginManager().hasPlugin("stano-java-conventions"));
    assertTrue(childProject.getPluginManager().hasPlugin("java-library"));
    assertTrue(childProject.getPluginManager().hasPlugin("jacoco"));
    assertFalse(childProject.getPluginManager().hasPlugin("maven-publish"));
  }

  @Test
  void shouldVerifyThatSourceSetsAreConfiguredProperly() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(childProject.getTasks().findByName("compileJava") != null);
  }

  @Test
  void shouldVerifyThatRepositoriesAreConfiguredProperly() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(childProject.getRepositories().size() > 0);
  }

  @Test
  void shouldVerifyThatDefaultDependenciesAreConfigured() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(
        childProject.getConfigurations().getByName("implementation").getDependencies().size() >= 1);
  }

  @Test
  void shouldVerifyThatArtifactsAreConfiguredProperly() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(childProject.getTasks().findByName("jar") != null);
    assertTrue(childProject.getConfigurations().getByName("archives").getArtifacts().size() >= 1);
  }

  @Test
  void shouldVerifyThatCompilersAreConfiguredProperly() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(childProject.getTasks().findByName("compileJava") != null);
  }

  @Test
  void shouldVerifyThatTestPluginIsConfiguredProperly() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(childProject.getTasks().findByName("test") != null);
  }

  @Test
  void shouldVerifyThatJacocoPluginIsConfiguredProperly() {
    childProject.getPluginManager().apply("stano-java-conventions");
    assertTrue(childProject.getTasks().findByName("jacocoTestReport") != null);
  }
}
