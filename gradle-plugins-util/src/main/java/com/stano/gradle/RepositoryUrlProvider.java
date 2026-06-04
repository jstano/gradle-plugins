package com.stano.gradle;

import org.eclipse.jgit.api.Git;
import org.gradle.api.Project;

import java.io.IOException;

public class RepositoryUrlProvider {

   private final Project project;

   public RepositoryUrlProvider(Project project) {

      this.project = project;
   }

   @Override
   public String toString() {

      try (Git git = Git.open(project.getRootDir())) {
         return git.getRepository().getConfig().getString("remote", "origin", "url");
      }
      catch (IOException ignored) {
         return null;
      }
   }
}
