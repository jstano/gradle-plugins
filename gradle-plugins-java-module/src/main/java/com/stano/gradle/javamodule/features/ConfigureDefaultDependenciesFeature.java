package com.stano.gradle.javamodule.features;

import com.stano.gradle.PluginFeature;
import com.stano.gradle.RootExtension;
import com.stano.gradle.javamodule.JavaExtension;
import java.util.HashMap;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;

public class ConfigureDefaultDependenciesFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    final var javaExtension = project.getExtensions().getByType(JavaExtension.class);
    final var rootExtension =
        project.getRootProject().getExtensions().getByType(RootExtension.class);
    if (rootExtension.getMspVersion() != null) {
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
          dependencies.enforcedPlatform("com.stano:msp-bom:" + rootExtension.getMspVersion()));
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
    for (var config : project.getConfigurations()) {
      for (var dep : config.getDependencies()) {
        if ("org.mapstruct".equals(dep.getGroup()) && "mapstruct".equals(dep.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  private Map<String, String> exclude(String group, String module) {
    Map<String, String> map = new HashMap<>();
    map.put("group", group);
    map.put("module", module);
    return map;
  }
}
