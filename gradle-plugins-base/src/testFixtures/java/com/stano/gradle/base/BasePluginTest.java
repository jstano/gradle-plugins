package com.stano.gradle.base;

import com.stano.gradle.base.features.BaseExtensionFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BasePluginTest {
  protected Project rootProject;
  protected Project childProject;

  @BeforeEach
  void setupProjects() throws IOException {
    File tempDir = Files.createTempDirectory("gradle-test").toFile();
    rootProject = ProjectBuilder.builder().withName("root").withProjectDir(tempDir).build();
    rootProject.setVersion("1.2.3");
    rootProject.getProjectDir().mkdirs();
    new BaseExtensionFeature().apply(rootProject);
    File childDir = new File(rootProject.getProjectDir(), "child");
    childDir.mkdirs();
    childProject =
        ProjectBuilder.builder()
            .withName("child")
            .withProjectDir(childDir)
            .withParent(rootProject)
            .build();
    childProject.setVersion("1.2.3");
    childProject.getProjectDir().mkdirs();
  }

  @AfterEach
  void cleanup() throws IOException {
    Path tempDir = rootProject.getProjectDir().toPath();
    Files.walk(tempDir)
        .sorted((a, b) -> b.compareTo(a))
        .forEach(
            p -> {
              try {
                Files.delete(p);
              } catch (IOException e) {
                // Ignore
              }
            });
  }
}
