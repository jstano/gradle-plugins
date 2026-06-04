package com.stano.gradle.springboot;

import com.stano.gradle.RootExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.PluginContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SpringBootPlugin implements Plugin<Project> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootPlugin.class);

  @Override
  public void apply(Project project) {
    RootExtension rootExtension = project.getRootProject().getExtensions().getByType(RootExtension.class);

    if (rootExtension.getJavaPlatformVersion() != null) {
      project.getBuildscript().getDependencies().add("classpath", "com.r365:platform-spring-boot-gradle-plugin:" + rootExtension.getJavaPlatformVersion());

      PluginContainer plugins = project.getPlugins();
      plugins.apply("org.springframework.boot");

      DependencyHandler dependencies = project.getDependencies();
      dependencies.add("developmentOnly", dependencies.enforcedPlatform("com.r365:platform-bom:" + rootExtension.getJavaPlatformVersion()));
      dependencies.add("developmentOnly", "org.springframework.boot:spring-boot-devtools");
      dependencies.add("runtimeOnly", dependencies.enforcedPlatform("com.r365:platform-bom:" + rootExtension.getJavaPlatformVersion()));
      dependencies.add("runtimeOnly", "io.micrometer:micrometer-registry-prometheus");
      dependencies.add("implementation", "com.r365:platform-spring-boot-application:" + rootExtension.getJavaPlatformVersion());
      dependencies.add("testImplementation", "com.r365:platform-spring-test:" + rootExtension.getJavaPlatformVersion());
//    tasks.withType<BootBuildImage> {
//      builder = "paketobuildpacks/builder-jammy-base:latest"
//    }
    }
    else {
      LOGGER.warn("********** No javaPlatformVersion specified, not enabling the Spring Boot plugin **********");
    }
  }
}
