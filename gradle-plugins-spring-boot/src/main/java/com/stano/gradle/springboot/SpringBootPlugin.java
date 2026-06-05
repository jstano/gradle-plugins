package com.stano.gradle.springboot;

import com.stano.gradle.RootExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.gradle.tasks.bundling.BootJar;

class SpringBootPlugin implements Plugin<Project> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootPlugin.class);

  @Override
  public void apply(Project project) {
    RootExtension rootExtension = project.getRootProject().getExtensions().getByType(RootExtension.class);

    PluginContainer plugins = project.getPlugins();
    plugins.apply("org.springframework.boot");

    DependencyHandler dependencies = project.getDependencies();
    dependencies.add("developmentOnly", dependencies.enforcedPlatform("com.stano:platform-bom:" + rootExtension.getJavaPlatformVersion()));
    dependencies.add("developmentOnly", "org.springframework.boot:spring-boot-devtools");
    dependencies.add("runtimeOnly", dependencies.enforcedPlatform("com.stano:platform-bom:" + rootExtension.getJavaPlatformVersion()));
    dependencies.add("runtimeOnly", "io.micrometer:micrometer-registry-prometheus");
    dependencies.add("implementation", "com.stano:platform-spring-boot-application:" + rootExtension.getJavaPlatformVersion());
    dependencies.add("testImplementation", "com.stano:platform-spring-test:" + rootExtension.getJavaPlatformVersion());
//    tasks.withType<BootBuildImage> {
//      builder = "paketobuildpacks/builder-jammy-base:latest"
//    }

    TaskContainer tasks = project.getTasks();
    tasks.named("bootJar", BootJar.class, bootJar -> {
      bootJar.getArchiveBaseName().set(project.getRootProject().getName());
      bootJar.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE);
    });
  }
}
