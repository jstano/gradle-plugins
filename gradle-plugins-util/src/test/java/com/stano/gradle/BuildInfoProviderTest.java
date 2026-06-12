package com.stano.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class BuildInfoProviderTest {
  @Test
  void shouldReturnValuesFromEnvironment() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("BUILD_NUMBER")).thenReturn("123");
    when(environment.getEnvironmentVariable("BRANCH_NAME")).thenReturn("main");
    when(environment.getEnvironmentVariable("JOB_NAME")).thenReturn("job/dev/job/taps/job/main");
    BuildInfoProvider buildInfo = new BuildInfoProvider(environment);
    assertEquals("123", buildInfo.getBuildNumber());
    assertEquals("main", buildInfo.getBranchName());
    assertEquals("job/dev/job/taps/job/main", buildInfo.getJobName());
  }

  @Test
  void shouldReturnUnspecifiedIfEnvironmentMissing() {
    Environment environment = mock(Environment.class);
    BuildInfoProvider buildInfo = new BuildInfoProvider(environment);
    assertEquals("unspecified", buildInfo.getBuildNumber());
    assertEquals("unspecified", buildInfo.getBranchName());
    assertEquals("unspecified", buildInfo.getJobName());
  }
}
