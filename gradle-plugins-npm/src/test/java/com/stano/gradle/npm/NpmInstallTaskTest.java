package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.features.NpmExtensionFeature;
import java.io.File;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NpmInstallTaskTest extends BasePluginTest {
  private ExecOperations execOperations;

  @BeforeEach
  void setup() {
    new NpmExtensionFeature().apply(childProject);

    execOperations = mock(ExecOperations.class);
    ExecResult execResult = mock(ExecResult.class);
    when(execResult.getExitValue()).thenReturn(0);
    when(execOperations.exec(any())).thenReturn(execResult);
  }

  private NpmInstallTask createTask() {
    return childProject
        .getTasks()
        .register("npmInstall", NpmInstallTask.class, childProject, execOperations)
        .get();
  }

  @Test
  void shouldHaveTheBuildGroupAndAnInstallDescription() {
    NpmInstallTask task = createTask();

    assertEquals("build", task.getGroup());
    assertEquals("Install npm dependencies", task.getDescription());
  }

  @Test
  void inputFilesShouldBePackageJsonAndPackageLockJson() {
    NpmInstallTask task = createTask();

    var fileNames = task.getInputFiles().stream().map(File::getName).toList();
    assertTrue(fileNames.contains("package.json"));
    assertTrue(fileNames.contains("package-lock.json"));
  }

  @Test
  void outputDirectoryShouldBeNodeModules() {
    NpmInstallTask task = createTask();

    assertEquals("node_modules", task.getOutputDirectory().getName());
  }

  @Test
  void execShouldRunNpmInstall() {
    NpmInstallTask task = createTask();

    task.exec();

    verify(execOperations, times(1)).exec(any());
  }
}
