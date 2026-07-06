package com.stano.gradle.springboot.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.PluginFeature;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;

public class ConfigureOtelJavaagentFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    DependencyHandler dependencies = project.getDependencies();
    TaskContainer tasks = project.getTasks();

    Configuration otelAgent = project.getConfigurations().create("otelAgent");
    dependencies.add(
        "otelAgent",
        dependencies.enforcedPlatform("com.stano:msp-bom:" + baseExtension.getMspVersion()));
    dependencies.add("otelAgent", "io.opentelemetry.javaagent:opentelemetry-javaagent");

    tasks.register(
        "verifyOtelJavaagent",
        task -> {
          task.setGroup("Build");
          task.setDescription(
              "Fails the build if the opentelemetry-javaagent jar is not resolvable");
          task.doLast(
              t -> {
                if (otelAgent.isEmpty()) {
                  throw new GradleException(
                      "opentelemetry-javaagent jar not found in project dependencies");
                }
              });
        });

    tasks.register(
        "copyOtelJavaagent",
        Copy.class,
        task -> {
          task.setGroup("Build");
          task.setDescription("Copies opentelemetry-javaagent jar to build/otel");
          task.dependsOn("verifyOtelJavaagent");
          task.from(otelAgent);
          task.into(project.getLayout().getBuildDirectory().dir("otel"));
        });

    tasks.named("assemble").configure(t -> t.dependsOn("copyOtelJavaagent"));
    project.afterEvaluate(
        p -> p.getTasks().named("bootJar").configure(t -> t.dependsOn("copyOtelJavaagent")));
  }
}
