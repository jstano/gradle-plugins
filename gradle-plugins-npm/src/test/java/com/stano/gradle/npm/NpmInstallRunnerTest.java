package com.stano.gradle.npm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NpmInstallRunnerTest {
  @TempDir File packageDir;

  @Test
  void npmInstallShouldOnlyRunOncePerDirectoryPerBuild() {
    ExecOperations execOperations = mock(ExecOperations.class);
    ExecResult execResult = mock(ExecResult.class);
    when(execResult.getExitValue()).thenReturn(0);
    when(execOperations.exec(any())).thenReturn(execResult);

    NpmInstallRunner.npmInstall(execOperations, packageDir, false, "20.11.0");
    NpmInstallRunner.npmInstall(execOperations, packageDir, false, "20.11.0");

    verify(execOperations, times(1)).exec(any());
  }
}
