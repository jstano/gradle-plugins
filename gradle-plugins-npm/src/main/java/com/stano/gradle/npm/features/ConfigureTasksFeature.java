package com.stano.gradle.npm.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.npm.NpmBuildTask;
import com.stano.gradle.npm.NpmCleanTask;
import com.stano.gradle.npm.NpmInstallTask;
import com.stano.gradle.npm.NpmTestTask;
import com.stano.gradle.npm.NpmVersionTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

public class ConfigureTasksFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    TaskContainer tasks = project.getTasks();

    tasks.register("npmVersion", NpmVersionTask.class, project);
    tasks.register("npmInstall", NpmInstallTask.class, project);

    TaskProvider<NpmCleanTask> npmCleanTask =
        tasks.register("npmClean", NpmCleanTask.class, project);
    // "clean" only exists once a lifecycle-base-providing plugin (e.g. java) is applied, which
    // may happen before or after this plugin. Deferring via withPlugin avoids both an
    // UnknownTaskException (if clean doesn't exist yet) and a DuplicateTaskException (if we
    // eagerly created a placeholder "clean" that then collides with the real one).
    project
        .getPluginManager()
        .withPlugin(
            "base", plugin -> tasks.named("clean").configure(task -> task.dependsOn(npmCleanTask)));

    tasks.register("npmRunBuild", NpmBuildTask.class, project);
    tasks.register("npmTest", NpmTestTask.class, project);
  }
}
