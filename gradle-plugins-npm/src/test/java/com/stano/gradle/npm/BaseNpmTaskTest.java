package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stano.gradle.base.Environment;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BaseNpmTaskTest {
  @Test
  void shouldSetNodeOptionsWhenNotAlreadySet() {
    Environment environment = mock(Environment.class);
    when(environment.getAllEnvironmentVariables()).thenReturn(Map.of());

    Map<String, String> result = BaseNpmTask.getDefaultNodeEnvironment(environment);

    assertEquals("--max_old_space_size=4096", result.get("NODE_OPTIONS"));
  }

  @Test
  void shouldAppendMaxOldSpaceSizeWhenNodeOptionsIsSetWithoutIt() {
    Environment environment = mock(Environment.class);
    when(environment.getAllEnvironmentVariables())
        .thenReturn(Map.of("NODE_OPTIONS", "--trace-warnings"));

    Map<String, String> result = BaseNpmTask.getDefaultNodeEnvironment(environment);

    assertEquals("--trace-warnings --max_old_space_size=4096", result.get("NODE_OPTIONS"));
  }

  @Test
  void shouldLeaveNodeOptionsUnchangedWhenMaxOldSpaceSizeIsAlreadySet() {
    Environment environment = mock(Environment.class);
    when(environment.getAllEnvironmentVariables())
        .thenReturn(Map.of("NODE_OPTIONS", "--max_old_space_size=8192"));

    Map<String, String> result = BaseNpmTask.getDefaultNodeEnvironment(environment);

    assertEquals("--max_old_space_size=8192", result.get("NODE_OPTIONS"));
  }
}
