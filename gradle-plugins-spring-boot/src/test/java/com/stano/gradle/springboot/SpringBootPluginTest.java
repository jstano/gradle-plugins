package com.stano.gradle.springboot;

import com.stano.gradle.RootExtensionFeature;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringBootPluginTest {
  @Test
  void shouldConfigureThingsProperly() {
    var project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("mspVersion", "1.0.0");
    new RootExtensionFeature().apply(project);
    project.getPluginManager().apply("java");
    project.getPluginManager().apply("com.stano.spring-boot");
    assertTrue(project.getPluginManager().hasPlugin("org.springframework.boot"));
  }

  @Test
  void shouldRegisterCopyOtelJavaagentTask() {
    var project = ProjectBuilder.builder().build();
    project.getExtensions().getExtraProperties().set("mspVersion", "1.0.0");
    new RootExtensionFeature().apply(project);
    project.getPluginManager().apply("java");
    project.getPluginManager().apply("com.stano.spring-boot");
    assertTrue(project.getTasks().getNames().contains("copyOtelJavaagent"));
  }
}
