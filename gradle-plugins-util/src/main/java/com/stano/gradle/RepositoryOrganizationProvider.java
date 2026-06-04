package com.stano.gradle;

import org.eclipse.jgit.api.Git;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class RepositoryOrganizationProvider {

   private final Project project;

   public RepositoryOrganizationProvider(Project project) {

      this.project = project;
   }

   @Override
   public String toString() {

      try (Git git = Git.open(project.getRootDir())) {
         String repositoryUrl = git.getRepository().getConfig().getString("remote", "origin", "url");

         if (repositoryUrl.startsWith("http")) {
            try {
               String path = new URI(repositoryUrl).toURL().getPath();
               return path.substring(1, path.indexOf("/", 1));
            }
            catch (URISyntaxException | MalformedURLException x) {
               throw new GradleException(String.format("Repository URL '%s' is invalid", repositoryUrl), x);
            }
         }

         if (repositoryUrl.startsWith("ssh://")) {
            int organizationStartIndex = repositoryUrl.indexOf("/", 6) + 1;
            return repositoryUrl.substring(organizationStartIndex, repositoryUrl.indexOf("/", organizationStartIndex));
         }

         int organizationStartIndex = repositoryUrl.indexOf(":") + 1;
         return repositoryUrl.substring(organizationStartIndex, repositoryUrl.indexOf("/", organizationStartIndex));
      }
      catch (IOException ignored) {
         return null;
      }
   }
}
