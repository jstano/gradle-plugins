package com.stano.gradle.base;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.eclipse.jgit.api.Git;
import org.gradle.api.Project;

public class RepositoryUrlProvider implements Serializable {
  private final File gitRootDir;

  public RepositoryUrlProvider(File gitRootDir) {
    this.gitRootDir = gitRootDir;
  }

  @Deprecated
  public RepositoryUrlProvider(Project project) {
    this(project.getRootDir());
  }

  @Override
  public String toString() {
    try (Git git = Git.open(gitRootDir)) {
      return git.getRepository().getConfig().getString("remote", "origin", "url");
    } catch (IOException ignored) {
      return null;
    }
  }
}
