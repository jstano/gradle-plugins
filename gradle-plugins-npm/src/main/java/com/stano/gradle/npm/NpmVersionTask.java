package com.stano.gradle.npm;

import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "no meaningful inputs or outputs to cache")
public class NpmVersionTask extends BaseNpmTask {
  @Inject
  public NpmVersionTask(Project project, ExecOperations execOperations) {
    super(project, execOperations, "other", "Show npm version", "--version");

    setCIToTrueInRunnerEnvironment();
  }

  @TaskAction
  public void exec() {
    super.exec();
  }
}
