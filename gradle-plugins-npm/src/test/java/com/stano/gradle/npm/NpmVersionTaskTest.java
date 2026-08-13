package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.features.NpmExtensionFeature;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NpmVersionTaskTest extends BasePluginTest {
  private ExecOperations execOperations;

  @BeforeEach
  void setup() {
    new NpmExtensionFeature().apply(childProject);

    execOperations = mock(ExecOperations.class);
    ExecResult execResult = mock(ExecResult.class);
    when(execResult.getExitValue()).thenReturn(0);
    when(execOperations.exec(any())).thenReturn(execResult);
  }

  private NpmVersionTask createTask() {
    return childProject
        .getTasks()
        .register("npmVersion", NpmVersionTask.class, childProject, execOperations)
        .get();
  }

  @Test
  void shouldHaveTheOtherGroupAndAVersionDescription() {
    NpmVersionTask task = createTask();

    assertEquals("other", task.getGroup());
    assertEquals("Show npm version", task.getDescription());
  }

  @Test
  void shouldRunWithCiSetToTrue() {
    NpmVersionTask task = createTask();

    assertEquals("true", task.getEnvironment().get("CI"));
  }

  @Test
  void shouldRunWithTheVersionArgument() {
    NpmVersionTask task = createTask();

    assertTrue(task.getArguments().contains("--version"));
  }

  @Test
  void execShouldNotThrowWhenNpmSucceeds() {
    NpmVersionTask task = createTask();

    assertDoesNotThrow(task::exec);
  }
}
