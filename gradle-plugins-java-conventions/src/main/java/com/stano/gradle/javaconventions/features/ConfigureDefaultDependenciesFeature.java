package com.stano.gradle.javaconventions.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.javaconventions.JavaConventionsExtension;
import java.util.HashMap;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;

public class ConfigureDefaultDependenciesFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    final var javaExtension = project.getExtensions().getByType(JavaConventionsExtension.class);
    final var baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    if (baseExtension.getMspVersion() != null) {
      project
          .getConfigurations()
          .forEach(
              it -> {
                it.exclude(exclude("commons-logging", "commons-logging"));
                it.exclude(exclude("commons-logging", "commons-logging-api"));
                it.exclude(exclude("log4j", "log4j"));
              });
      project
          .getConfigurations()
          .getByName("compileClasspath")
          .exclude(exclude("jakarta.transaction", "jakarta.transaction-api"));
      DependencyHandler dependencies = project.getDependencies();
      dependencies.add(
          "implementation",
          dependencies.enforcedPlatform("com.stano:msp-bom:" + baseExtension.getMspVersion()));
      dependencies.add(
          "annotationProcessor",
          dependencies.enforcedPlatform("com.stano:msp-bom:" + baseExtension.getMspVersion()));
      dependencies.add("compileOnly", "org.jetbrains:annotations");
      dependencies.add("testImplementation", "com.stano:msp-test-starter");
      dependencies.add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher");
    }
    configureMapStructAnnotationProcessor(project);
  }

  private void configureMapStructAnnotationProcessor(Project project) {
    project.afterEvaluate(
        p -> {
          if (hasMapStructDependency(p)) {
            p.getDependencies().add("annotationProcessor", "org.mapstruct:mapstruct-processor");
          }
        });
  }

  private boolean hasMapStructDependency(Project project) {
    try {
      return project.getConfigurations().getByName("compileClasspath")
          .getResolvedConfiguration().getResolvedArtifacts().stream()
          .anyMatch(
              a ->
                  "org.mapstruct".equals(
                          a.getModuleVersion().getId().getModule().getGroup())
                      && "mapstruct".equals(
                          a.getModuleVersion().getId().getModule().getName()));
    } catch (Exception e) {
      return false;
    }
  }

  private Map<String, String> exclude(String group, String module) {
    Map<String, String> map = new HashMap<>();
    map.put("group", group);
    map.put("module", module);
    return map;
  }
}
