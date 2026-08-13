package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.features.NpmExtensionFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NpmTestTaskTest extends BasePluginTest {
  private ExecOperations execOperations;

  @BeforeEach
  void setup() {
    new NpmExtensionFeature().apply(childProject);

    execOperations = mock(ExecOperations.class);
    ExecResult execResult = mock(ExecResult.class);
    when(execResult.getExitValue()).thenReturn(0);
    when(execOperations.exec(any())).thenReturn(execResult);
  }

  private void writePackageJson(String contents) throws IOException {
    Files.writeString(new File(childProject.getProjectDir(), "package.json").toPath(), contents);
  }

  private NpmTestTask createTask() {
    return childProject
        .getTasks()
        .register("npmTest", NpmTestTask.class, childProject, execOperations)
        .get();
  }

  @Test
  void shouldRunPlainTestByDefault() throws IOException {
    writePackageJson("{\"scripts\": {\"test\": \"jest\"}}");

    NpmTestTask task = createTask();

    assertEquals("verification", task.getGroup());
    assertTrue(task.getArguments().contains("test"));
  }

  @Test
  void shouldRunTestWithCoverageWhenThatScriptExists() throws IOException {
    writePackageJson("{\"scripts\": {\"test:withCoverage\": \"jest --coverage\"}}");

    NpmTestTask task = createTask();

    assertTrue(task.getArguments().contains("run"));
    assertTrue(task.getArguments().contains("test:withCoverage"));
  }

  @Test
  void outputDirectoryShouldDefaultToCoverage() throws IOException {
    writePackageJson("{\"scripts\": {\"test\": \"jest\"}}");

    NpmTestTask task = createTask();

    assertEquals(new File(childProject.getProjectDir(), "coverage"), task.getOutputDirectory());
  }

  @Test
  void outputDirectoryShouldUseBuildCoverageWhenConfigured() throws IOException {
    writePackageJson("{\"scripts\": {\"test\": \"jest --coverageDirectory=build/coverage\"}}");

    NpmTestTask task = createTask();

    assertEquals(
        new File(childProject.getProjectDir(), "build/coverage"), task.getOutputDirectory());
  }

  @Test
  void execShouldWriteACoverageMarkerFileWhenNotRunningWithCoverage() throws IOException {
    writePackageJson("{\"scripts\": {\"test\": \"jest\"}}");
    // Simulate npm test having already written coverage output before the marker is written.
    new File(childProject.getProjectDir(), "coverage").mkdirs();
    NpmTestTask task = createTask();

    task.exec();

    File markerFile = new File(childProject.getProjectDir(), "coverage/coverage.txt");
    assertTrue(markerFile.exists());
  }
}
