package com.stano.gradle.npm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
public class NpmTestTask extends BaseNpmTask {
  private final ExecOperations execOperations;

  @Inject
  public NpmTestTask(Project project, ExecOperations execOperations) {
    super(
        project,
        execOperations,
        "verification",
        "Run the tests",
        getTestCommand(project.getProjectDir()));

    this.execOperations = execOperations;

    setCIToTrueInRunnerEnvironment();
  }

  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public List<File> getInputFiles() {
    Project project = getProject();
    List<File> files = new ArrayList<>();

    files.add(project.file(new File(projectDir, "package.json")));
    files.add(project.file(new File(projectDir, "package-lock.json")));

    File configOverrides = new File(projectDir, "config-overrides.js");
    if (project.file(configOverrides).exists()) {
      files.add(configOverrides);
    }

    File tsconfigTest = new File(projectDir, "tsconfig.test.json");
    if (project.file(tsconfigTest).exists()) {
      files.add(project.file(tsconfigTest));
    }

    return files;
  }

  @Optional
  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public File getPublicDirectory() {
    File publicDir = new File(projectDir, "public");
    if (publicDir.exists()) {
      return getProject().file(publicDir);
    }
    return null;
  }

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public File getSrcDirectory() {
    return getProject().file(new File(projectDir, "src"));
  }

  @OutputDirectory
  public File getOutputDirectory() {
    return getCoverageFolder(projectDir);
  }

  @TaskAction
  public void exec() {
    NpmInstallRunner.npmInstall(
        execOperations,
        projectDir,
        npmExtension.getUseNvm().get(),
        npmExtension.getNodeVersion().get());

    super.exec();

    if (!getArguments().contains("test:withCoverage")) {
      try {
        File coverageTextFile = new File(projectDir, "coverage/coverage.txt");
        Files.write(coverageTextFile.toPath(), "true".getBytes());
      } catch (IOException ignored) {
        // Ignore
      }
    }
  }

  private static String[] getTestCommand(File projectDir) {
    try {
      File packageJsonFile = new File(projectDir, "package.json");
      String packageJsonText = new String(Files.readAllBytes(packageJsonFile.toPath()));

      if (packageJsonText.contains("test:withCoverage")) {
        return new String[] {"run", "test:withCoverage"};
      }
    } catch (IOException ignored) {
      // Ignore
    }

    return new String[] {"test"};
  }

  private static File getCoverageFolder(File projectDir) {
    try {
      File packageJsonFile = new File(projectDir, "package.json");
      String packageJsonText = new String(Files.readAllBytes(packageJsonFile.toPath()));

      if (packageJsonText.contains("--coverageDirectory=build/coverage")) {
        return new File(projectDir, "build/coverage");
      }
    } catch (IOException ignored) {
      // Ignore
    }

    return new File(projectDir, "coverage");
  }
}
