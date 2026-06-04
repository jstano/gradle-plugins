package com.stano.gradle;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.gradle.api.Project;

import java.io.IOException;

public class CommitHashProvider {

   private final Project project;

   public CommitHashProvider(Project project) {

      this.project = project;
   }

   @Override
   public String toString() {

      try (Git git = Git.open(project.getRootDir())) {
         Ref head = git.getRepository().getRefDatabase().findRef("HEAD");

         if (head != null) {
            ObjectId objectId = head.getObjectId();

            if (objectId != null) {
               try (ObjectReader objectReader = git.getRepository().newObjectReader()) {
                  return objectReader.abbreviate(objectId, 8).name();
               }
            }
         }
      }
      catch (IOException ignored) {
      }

      return null;
   }
}
