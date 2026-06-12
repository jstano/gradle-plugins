package com.stano.gradle;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gradle.api.Project;

public class CommitTimeProvider {
  private final Project project;

  public CommitTimeProvider(Project project) {
    this.project = project;
  }

  @Override
  public String toString() {
    try (Git git = Git.open(project.getRootDir())) {
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
