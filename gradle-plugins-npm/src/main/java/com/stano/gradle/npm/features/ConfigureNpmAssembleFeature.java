package com.stano.gradle.npm.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.npm.NpmAssembleTask;
import com.stano.gradle.npm.NpmPluginUtils;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.tasks.Jar;

public class ConfigureNpmAssembleFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    TaskContainer tasks = project.getTasks();

    NpmAssembleTask npmAssembleTask =
        tasks.register("npmAssemble", NpmAssembleTask.class, project).get();
    npmAssembleTask.dependsOn(tasks.named("npmRunBuild"));

    project
        .getPluginManager()
        .withPlugin(
            "java",
            plugin -> {
              if (NpmPluginUtils.shouldExecute(project)) {
                Jar jarTask = (Jar) tasks.getByName("jar");
                jarTask.dependsOn(npmAssembleTask);

                Task testTask = tasks.getByName("test");
                testTask.dependsOn(tasks.named("npmTest"));
              }
            });
  }
}
