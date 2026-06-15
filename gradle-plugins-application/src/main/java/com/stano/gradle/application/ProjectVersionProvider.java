package com.stano.gradle.application;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.CommitHashProvider;
import com.stano.gradle.base.CommitTimeProvider;
import java.io.Serializable;
import org.gradle.api.Project;

public class ProjectVersionProvider implements Serializable {
  private final transient Project project;
  private final transient BaseExtension baseExtension;
  private String version;

  public ProjectVersionProvider(Project project, BaseExtension baseExtension) {
    this.project = project;
    this.baseExtension = baseExtension;
  }

  @Override
  public String toString() {
    if (version == null) {
      String commitTimestamp = new CommitTimeProvider(project).toString();
      String commitHash = new CommitHashProvider(project).toString();
      if (commitTimestamp != null && commitHash != null) {
        String buildNumber = baseExtension.getBuildNumber();
        if (buildNumber != null) {
          return String.format("%s-%s-%s", commitTimestamp, commitHash, buildNumber);
        }
        return String.format("%s-%s", commitTimestamp, commitHash);
      }
      version = baseExtension.getBuildTimeFormatted();
    }
    return version;
  }
}
