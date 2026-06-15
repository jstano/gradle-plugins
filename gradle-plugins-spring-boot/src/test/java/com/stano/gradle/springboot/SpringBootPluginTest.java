package com.stano.gradle.springboot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.features.BaseExtensionFeature;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class SpringBootPluginTest {
  @Test
  void shouldConfigureThingsProperly() {
    var project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("mspVersion", "1.0.0");
    new BaseExtensionFeature().apply(project);
    project.getPluginManager().apply("java");
    project.getPluginManager().apply("com.stano.spring-boot");
    assertTrue(project.getPluginManager().hasPlugin("org.springframework.boot"));
  }

  @Test
  void shouldRegisterCopyOtelJavaagentTask() {
    var project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("mspVersion", "1.0.0");
    new BaseExtensionFeature().apply(project);
    project.getPluginManager().apply("java");
    project.getPluginManager().apply("com.stano.spring-boot");
    assertTrue(project.getTasks().getNames().contains("copyOtelJavaagent"));
  }

  @Test
  void shouldConfigureProcessResourcesTask() {
    var project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("mspVersion", "1.0.0");
    new BaseExtensionFeature().apply(project);
    project.getPluginManager().apply("java");
    project.getPluginManager().apply("com.stano.spring-boot");
    assertTrue(project.getTasks().getNames().contains("processResources"));
  }
}
