package com.stano.gradle;

import org.eclipse.jgit.api.Git;
import org.gradle.api.Project;

import java.io.IOException;

public class BranchNameProvider {
  private final Project project;

  public BranchNameProvider(Project project) {
    this.project = project;
  }

  @Override
  public String toString() {
    String branchName = System.getenv("CHANGE_BRANCH");
    if (branchName == null) {
      branchName = System.getenv("BRANCH_NAME");
    }
    if (branchName == null) {
      try (Git git = Git.open(project.getRootDir())) {
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
