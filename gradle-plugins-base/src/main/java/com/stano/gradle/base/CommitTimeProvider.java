package com.stano.gradle.base;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class CommitTimeProvider implements Serializable {
  private final File gitRootDir;

  public CommitTimeProvider(File gitRootDir) {
    this.gitRootDir = gitRootDir;
  }

  @Override
  public String toString() {
    try (Git git = Git.open(gitRootDir)) {
      Ref head = git.getRepository().getRefDatabase().findRef("HEAD");
      if (head != null && head.getObjectId() != null) {
        RevCommit commit = new RevWalk(git.getRepository()).parseCommit(head.getObjectId());
        PersonIdent authorIdent = commit.getAuthorIdent();
        Instant authorDate = authorIdent.getWhenAsInstant();
        TimeZone authorTimeZone = TimeZone.getTimeZone(authorIdent.getZoneId());
        LocalDateTime commitTime = LocalDateTime.ofInstant(authorDate, authorTimeZone.toZoneId());
        return commitTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
      }
    } catch (IOException ignored) {
    }
    return null;
  }
}
