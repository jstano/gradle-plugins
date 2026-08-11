package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BranchNameProviderTest {
  @TempDir File gitRootDir;

  @Test
  void shouldReturnBranchFromGitlabEnvironment() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("CI_COMMIT_BRANCH")).thenReturn("release/1.0");
    BranchNameProvider branchNameProvider = new BranchNameProvider(gitRootDir, environment);
    assertEquals("release/1.0", branchNameProvider.toString());
  }

  @Test
  void shouldReturnBranchFromGithubEnvironment() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("GITHUB_REF_NAME")).thenReturn("feature/y");
    BranchNameProvider branchNameProvider = new BranchNameProvider(gitRootDir, environment);
    assertEquals("feature/y", branchNameProvider.toString());
  }

  @Test
  void shouldReturnBranchFromJenkinsChangeBranchAsFallback() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("CHANGE_BRANCH")).thenReturn("feature/x");
    when(environment.getEnvironmentVariable("BRANCH_NAME")).thenReturn("main");
    BranchNameProvider branchNameProvider = new BranchNameProvider(gitRootDir, environment);
    assertEquals("feature/x", branchNameProvider.toString());
  }

  @Test
  void shouldReturnBranchFromJenkinsBranchNameAsFallback() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("BRANCH_NAME")).thenReturn("develop");
    BranchNameProvider branchNameProvider = new BranchNameProvider(gitRootDir, environment);
    assertEquals("develop", branchNameProvider.toString());
  }

  @Test
  void shouldPreferGitlabOverGithubAndJenkins() {
    Environment environment = mock(Environment.class);
    when(environment.getEnvironmentVariable("CI_COMMIT_BRANCH")).thenReturn("gitlab-branch");
    when(environment.getEnvironmentVariable("GITHUB_REF_NAME")).thenReturn("github-branch");
    when(environment.getEnvironmentVariable("BRANCH_NAME")).thenReturn("jenkins-branch");
    BranchNameProvider branchNameProvider = new BranchNameProvider(gitRootDir, environment);
    assertEquals("gitlab-branch", branchNameProvider.toString());
  }

  @Test
  void shouldFallBackToGitBranchWhenNoEnvironmentVariablesAreSet() throws Exception {
    try (Git git = Git.init().setDirectory(gitRootDir).setInitialBranch("trunk").call()) {
      Environment environment = mock(Environment.class);
      BranchNameProvider branchNameProvider = new BranchNameProvider(gitRootDir, environment);
      assertEquals("trunk", branchNameProvider.toString());
    }
  }

  @Test
  void shouldFallBackToMainWhenNoEnvironmentVariablesAndNoGitRepository() {
    Environment environment = mock(Environment.class);
    BranchNameProvider branchNameProvider = new BranchNameProvider(gitRootDir, environment);
    assertEquals("main", branchNameProvider.toString());
  }
}
