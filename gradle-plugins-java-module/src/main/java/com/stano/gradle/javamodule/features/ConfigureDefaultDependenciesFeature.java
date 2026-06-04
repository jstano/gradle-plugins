package com.stano.gradle.javamodule.features;

import com.stano.gradle.PluginFeature;
import com.stano.gradle.RootExtension;
import com.stano.gradle.javamodule.JavaExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import java.util.HashMap;
import java.util.Map;

public class ConfigureDefaultDependenciesFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    final var r365JavaExtension = project.getExtensions().getByType(JavaExtension.class);
    final var r365Extension = project.getRootProject().getExtensions().getByType(RootExtension.class);

    if (r365Extension.getJavaPlatformVersion() != null) {
      project.getConfigurations().forEach(it -> {
        it.exclude(exclude("commons-logging", "commons-logging"));
        it.exclude(exclude("commons-logging", "commons-logging-api"));
        it.exclude(exclude("log4j", "log4j"));
      });

      DependencyHandler dependencies = project.getDependencies();
      dependencies.add("implementation", dependencies.enforcedPlatform("com.r365:platform-bom:" + r365Extension.getJavaPlatformVersion()));
      dependencies.add("compileOnly", "org.jetbrains:annotations:26.0.2");
      dependencies.add("testImplementation", "com.r365:platform-test");
      dependencies.add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher");
    }
  }

  private Map<String, String> exclude(String group, String module) {
    Map<String, String> map = new HashMap<>();
    map.put("group", group);
    map.put("module", module);
    return map;
  }
}
