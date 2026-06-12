package com.stano.gradle.docker;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.work.DisableCachingByDefault;

import javax.inject.Inject;
import java.io.File;

@DisableCachingByDefault(because = "docker compose has no input or outputs")
public class DockerComposeDown extends DefaultTask {
  @Internal private Configuration configuration;
  @Internal private final ExecOperations execOperations;

  public Configuration getConfiguration() {
    return configuration;
  }

  public ExecOperations getExecOperations() {
    return execOperations;
  }

  @Inject
  public DockerComposeDown(ExecOperations execOperations) {
    this.setGroup("Docker");
    this.execOperations = execOperations;
  }

  @TaskAction
  void run() {
    GradleExecUtils.execWithErrorMessage(
        getProject(),
        execOperations,
        spec -> {
          spec.executable("docker-compose");
          spec.args("-f", getDockerComposeFile().getAbsolutePath(), "down");
        });
  }

  @InputFiles
  @PathSensitive(PathSensitivity.NONE)
  File getDockerComposeFile() {
    return getDockerComposeExtension().getDockerComposeFile();
  }

  @Internal
  DockerComposeExtension getDockerComposeExtension() {
    return getProject().getExtensions().findByType(DockerComposeExtension.class);
  }
}
