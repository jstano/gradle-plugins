package com.stano.gradle.npm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(
    because = "node_modules output may contain platform-specific native binaries")
public class NpmInstallTask extends DefaultTask {
  private final File projectDir;
  private final boolean useNvm;
  private final String nodeVersion;
  private final ExecOperations execOperations;

  @Inject
  public NpmInstallTask(Project project, ExecOperations execOperations) {
    setGroup("build");
    setDescription("Install npm dependencies");

    NpmExtension npmExtension = project.getExtensions().getByType(NpmExtension.class);
    this.projectDir = npmExtension.getProjectDirectory().get();
    this.useNvm = npmExtension.getUseNvm().get();
    this.nodeVersion = npmExtension.getNodeVersion().get();
    this.execOperations = execOperations;
  }

  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public List<File> getInputFiles() {
    Project project = getProject();
    List<File> files = new ArrayList<>();

    files.add(project.file(new File(projectDir, "package.json")));
    files.add(project.file(new File(projectDir, "package-lock.json")));

    return files;
  }

  @OutputDirectory
  public File getOutputDirectory() {
    return getProject().file(new File(projectDir, "node_modules"));
  }

  @TaskAction
  public void exec() {
    NpmInstallRunner.npmInstall(execOperations, projectDir, useNvm, nodeVersion);
  }
}
