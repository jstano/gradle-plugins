package com.stano.gradle.base;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.gradle.api.Project;

public class CommitHashProvider implements Serializable {
  private final File gitRootDir;

  public CommitHashProvider(File gitRootDir) {
    this.gitRootDir = gitRootDir;
  }

  @Deprecated
  public CommitHashProvider(Project project) {
    this(project.getRootDir());
  }

  @Override
  public String toString() {
    try (Git git = Git.open(gitRootDir)) {
      Ref head = git.getRepository().getRefDatabase().findRef("HEAD");
      if (head != null) {
        ObjectId objectId = head.getObjectId();
        if (objectId != null) {
          try (ObjectReader objectReader = git.getRepository().newObjectReader()) {
            return objectReader.abbreviate(objectId, 8).name();
          }
        }
      }
    } catch (IOException ignored) {
    }
    return null;
  }
}
