package com.stano.gradle.springboot;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.springboot.features.ConfigureBuildInfoFeature;
import com.stano.gradle.springboot.features.ConfigureOtelJavaagentFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskContainer;
import org.springframework.boot.gradle.tasks.bundling.BootJar;

public class SpringBootPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new ConfigureBuildInfoFeature().apply(project);
    new ConfigureOtelJavaagentFeature().apply(project);
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    PluginContainer plugins = project.getPlugins();
    plugins.apply("org.springframework.boot");
    DependencyHandler dependencies = project.getDependencies();
    dependencies.add(
        "developmentOnly",
        dependencies.enforcedPlatform("com.stano:msp-bom:" + baseExtension.getMspVersion()));
    dependencies.add("developmentOnly", "org.springframework.boot:spring-boot-devtools");
    dependencies.add(
        "runtimeOnly",
        dependencies.enforcedPlatform("com.stano:msp-bom:" + baseExtension.getMspVersion()));
    dependencies.add("runtimeOnly", "io.micrometer:micrometer-registry-prometheus");
    dependencies.add(
        "implementation", "com.stano:msp-spring-boot-application:" + baseExtension.getMspVersion());
    dependencies.add(
        "testImplementation", "com.stano:msp-spring-test-starter:" + baseExtension.getMspVersion());
    TaskContainer tasks = project.getTasks();
    tasks.named(
        "bootJar",
        BootJar.class,
        bootJar -> {
          bootJar.getArchiveBaseName().set(project.getRootProject().getName());
          bootJar.setDuplicatesStrategy(DuplicatesStrategy.FAIL);
        });
  }
}
