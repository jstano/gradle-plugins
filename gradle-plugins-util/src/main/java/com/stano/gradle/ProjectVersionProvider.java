package com.stano.gradle;

import java.io.Serializable;
import org.gradle.api.Project;

public class ProjectVersionProvider implements Serializable {
  private final transient Project project;
  private final transient RootExtension rootExtension;
  private String version;

  public ProjectVersionProvider(Project project, RootExtension rootExtension) {
    this.project = project;
    this.rootExtension = rootExtension;
  }

  @Override
  public String toString() {
    if (version == null) {
      String commitTimestamp = new CommitTimeProvider(project).toString();
      String commitHash = new CommitHashProvider(project).toString();
      if (commitTimestamp != null && commitHash != null) {
        String buildNumber = rootExtension.getBuildNumber();
        if (buildNumber != null) {
          return String.format("%s-%s-%s", commitTimestamp, commitHash, buildNumber);
        }
        return String.format("%s-%s", commitTimestamp, commitHash);
      }
      version = rootExtension.getBuildTimeFormatted();
    }
    return version;
  }
}
