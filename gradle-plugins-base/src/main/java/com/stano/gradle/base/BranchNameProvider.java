package com.stano.gradle.base;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.eclipse.jgit.api.Git;
import org.gradle.api.Project;

public class BranchNameProvider implements Serializable {
  private final File gitRootDir;

  public BranchNameProvider(File gitRootDir) {
    this.gitRootDir = gitRootDir;
  }

  @Deprecated
  public BranchNameProvider(Project project) {
    this(project.getRootDir());
  }

  @Override
  public String toString() {
    String branchName = System.getenv("CHANGE_BRANCH");
    if (branchName == null) {
      branchName = System.getenv("BRANCH_NAME");
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
