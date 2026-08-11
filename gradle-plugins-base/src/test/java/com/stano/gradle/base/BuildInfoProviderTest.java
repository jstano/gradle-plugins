package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class BuildInfoProviderTest {
  @Test
  void shouldReturnValuesFromGitlabEnvironment() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("CI_PIPELINE_IID")).thenReturn("123");
    when(environment.getEnvironmentVariable("CI_COMMIT_BRANCH")).thenReturn("main");
    when(environment.getEnvironmentVariable("CI_JOB_NAME")).thenReturn("build");
    BuildInfoProvider buildInfo = new BuildInfoProvider(environment);
    assertEquals("123", buildInfo.getBuildNumber());
    assertEquals("main", buildInfo.getBranchName());
    assertEquals("build", buildInfo.getJobName());
  }

  @Test
  void shouldReturnValuesFromGithubEnvironment() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("GITHUB_RUN_NUMBER")).thenReturn("123");
    when(environment.getEnvironmentVariable("GITHUB_REF_NAME")).thenReturn("main");
    when(environment.getEnvironmentVariable("GITHUB_JOB")).thenReturn("build");
    BuildInfoProvider buildInfo = new BuildInfoProvider(environment);
    assertEquals("123", buildInfo.getBuildNumber());
    assertEquals("main", buildInfo.getBranchName());
    assertEquals("build", buildInfo.getJobName());
  }

  @Test
  void shouldReturnValuesFromJenkinsEnvironmentAsFallback() {
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
  void shouldPreferChangeBranchOverBranchNameForJenkins() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("CHANGE_BRANCH")).thenReturn("feature/x");
    when(environment.getEnvironmentVariable("BRANCH_NAME")).thenReturn("main");
    BuildInfoProvider buildInfo = new BuildInfoProvider(environment);
    assertEquals("feature/x", buildInfo.getBranchName());
  }

  @Test
  void shouldPreferGitlabOverGithubAndJenkins() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("CI_PIPELINE_IID")).thenReturn("gitlab-123");
    when(environment.getEnvironmentVariable("GITHUB_RUN_NUMBER")).thenReturn("github-123");
    when(environment.getEnvironmentVariable("BUILD_NUMBER")).thenReturn("jenkins-123");
    BuildInfoProvider buildInfo = new BuildInfoProvider(environment);
    assertEquals("gitlab-123", buildInfo.getBuildNumber());
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
