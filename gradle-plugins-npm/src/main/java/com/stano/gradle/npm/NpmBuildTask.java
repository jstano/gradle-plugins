package com.stano.gradle.npm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

@CacheableTask
public class NpmBuildTask extends BaseNpmTask {
  private final File outputDirectory;
  private final List<File> inputFiles;
  private final File publicDirectory;
  private final File srcDirectory;
  private final File resourceFilesDirectory;
  private final ExecOperations execOperations;

  @Inject
  public NpmBuildTask(Project project, ExecOperations execOperations) {
    super(project, execOperations, "build", "Build the package using npm", "run", "build");

    this.execOperations = execOperations;

    NpmExtension npmExtension = project.getExtensions().getByType(NpmExtension.class);
    outputDirectory = project.file(new File(npmExtension.getProjectDistRoot().get(), "dist"));
    inputFiles = inputFiles(project, projectDir);
    publicDirectory = publicDirectory(project, projectDir);
    srcDirectory = project.file(new File(projectDir, "src"));
    resourceFilesDirectory = resourceFilesDirectory(project, projectDir);

    setCIToTrueInRunnerEnvironment();
  }

  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public List<File> getInputFiles() {
    return inputFiles;
  }

  @Optional
  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public File getPublicDirectory() {
    return publicDirectory;
  }

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public File getSrcDirectory() {
    return srcDirectory;
  }

  @Optional
  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public File getResourceFilesDirectory() {
    return resourceFilesDirectory;
  }

  @OutputDirectory
  public File getOutputDirectory() {
    return outputDirectory;
  }

  @TaskAction
  public void exec() {
    NpmInstallRunner.npmInstall(
        execOperations,
        projectDir,
        npmExtension.getUseNvm().get(),
        npmExtension.getNodeVersion().get());

    super.exec();
  }

  private static List<File> inputFiles(Project project, File projectDir) {
    List<File> files = new ArrayList<>();

    files.add(project.file(new File(projectDir, "package.json")));
    files.add(project.file(new File(projectDir, "package-lock.json")));

    File configOverrides = new File(projectDir, "config-overrides.js");
    if (project.file(configOverrides).exists()) {
      files.add(configOverrides);
    }

    File tsconfig = new File(projectDir, "tsconfig.json");
    if (project.file(tsconfig).exists()) {
      files.add(project.file(tsconfig));
    }

    File tsconfigTest = new File(projectDir, "tsconfig.test.json");
    if (project.file(tsconfigTest).exists()) {
      files.add(project.file(tsconfigTest));
    }

    File tslint = new File(projectDir, "tslint.json");
    if (project.file(tslint).exists()) {
      files.add(project.file(tslint));
    }

    return files;
  }

  private static File publicDirectory(Project project, File projectDir) {
    File publicDir = new File(projectDir, "public");

    if (publicDir.exists()) {
      return project.file(publicDir);
    }
    return null;
  }

  private static File resourceFilesDirectory(Project project, File projectDir) {
    NpmExtension npmExtension = project.getExtensions().getByType(NpmExtension.class);
    String resourceFileDir = npmExtension.getResourceFilesDirectory().getOrNull();

    if (resourceFileDir != null && !resourceFileDir.isEmpty()) {
      return project.getRootProject().file(resourceFileDir);
    }

    return null;
  }
}
