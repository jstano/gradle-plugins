package com.stano.gradle.npm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "deletes files rather than producing a cacheable output")
public class NpmCleanTask extends DefaultTask {
  private final File projectDir;

  @Inject
  public NpmCleanTask(Project project) {
    setGroup("build");
    setDescription("Delete the coverage folder");

    NpmExtension npmExtension = project.getExtensions().getByType(NpmExtension.class);
    this.projectDir = npmExtension.getProjectDirectory().get();
  }

  @TaskAction
  public void exec() throws IOException {
    Path coverageDir = new File(projectDir, "coverage").toPath();

    if (Files.exists(coverageDir)) {
      try (Stream<Path> paths = Files.walk(coverageDir)) {
        paths
            .sorted(Comparator.reverseOrder())
            .forEach(
                path -> {
                  try {
                    Files.delete(path);
                  } catch (IOException ignored) {
                    // Ignore
                  }
                });
      }
    }
  }
}
