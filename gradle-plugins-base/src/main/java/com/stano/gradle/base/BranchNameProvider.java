package com.stano.gradle.base;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.eclipse.jgit.api.Git;
import org.gradle.api.Project;

public class BranchNameProvider implements Serializable {
  private final File gitRootDir;
  private final Environment environment;

  public BranchNameProvider(File gitRootDir) {
    this(gitRootDir, new SystemEnvironment());
  }

  BranchNameProvider(File gitRootDir, Environment environment) {
    this.gitRootDir = gitRootDir;
    this.environment = environment;
  }

  @Deprecated
  public BranchNameProvider(Project project) {
    this(project.getRootDir());
  }

  @Override
  public String toString() {
    String branchName = environment.getEnvironmentVariable("CI_COMMIT_BRANCH");
    if (branchName == null) {
      branchName = environment.getEnvironmentVariable("GITHUB_REF_NAME");
    }
    if (branchName == null) {
      branchName = environment.getEnvironmentVariable("CHANGE_BRANCH");
    }
    if (branchName == null) {
      branchName = environment.getEnvironmentVariable("BRANCH_NAME");
    }
    if (branchName == null) {
      try (Git git = Git.open(gitRootDir)) {
        branchName = git.getRepository().getBranch();
      } catch (IOException ignored) {
      }
    }
    if (branchName == null) {
      branchName = "main";
    }
    return branchName;
  }
}
