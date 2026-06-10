package com.stano.gradle.springboot;

import com.stano.gradle.RootExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.springframework.boot.gradle.tasks.bundling.BootJar;

class SpringBootPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    RootExtension rootExtension = project.getRootProject().getExtensions().getByType(RootExtension.class);

    PluginContainer plugins = project.getPlugins();
    plugins.apply("org.springframework.boot");

    DependencyHandler dependencies = project.getDependencies();
    dependencies.add("developmentOnly", dependencies.enforcedPlatform("com.stano:msp-bom:" + rootExtension.getMspVersion()));
    dependencies.add("developmentOnly", "org.springframework.boot:spring-boot-devtools");
    dependencies.add("runtimeOnly", dependencies.enforcedPlatform("com.stano:msp-bom:" + rootExtension.getMspVersion()));
    dependencies.add("runtimeOnly", "io.micrometer:micrometer-registry-prometheus");
    dependencies.add("implementation", "com.stano:msp-spring-boot-application:" + rootExtension.getMspVersion());
    dependencies.add("testImplementation", "com.stano:msp-spring-test-starter:" + rootExtension.getMspVersion());

    TaskContainer tasks = project.getTasks();
    tasks.named("bootJar", BootJar.class, bootJar -> {
      bootJar.getArchiveBaseName().set(project.getRootProject().getName());
      bootJar.setDuplicatesStrategy(DuplicatesStrategy.FAIL);
    });

    Configuration runtimeClasspath = project.getConfigurations().getByName("runtimeClasspath");
    FileCollection otelJavaagentJars = runtimeClasspath.filter(f -> f.getName().startsWith("opentelemetry-javaagent"));

    tasks.register("copyOtelJavaagent", Copy.class, task -> {
      task.setGroup("Build");
      task.setDescription("Copies opentelemetry-javaagent jar to build/libs");
      task.from(otelJavaagentJars);
      task.into(project.getLayout().getBuildDirectory().dir("libs"));
      task.doFirst(t -> {
        if (otelJavaagentJars.isEmpty()) {
          project.getLogger().warn("opentelemetry-javaagent jar not found in project dependencies");
        }
      });
    });

    tasks.named("assemble", t -> t.dependsOn("copyOtelJavaagent"));
  }
}
