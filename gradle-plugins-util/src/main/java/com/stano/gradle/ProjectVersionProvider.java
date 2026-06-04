package com.stano.gradle;

import org.gradle.api.Project;

import java.io.Serializable;

public class ProjectVersionProvider implements Serializable {
   private transient final Project project;
   private transient final RootExtension rootExtension;

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
            return String.format("%s-%s-%s", commitTimestamp, commitHash, rootExtension.getBuildNumber());
         }

         version = rootExtension.getBuildTimeFormatted();
      }

      return version;
   }
}
