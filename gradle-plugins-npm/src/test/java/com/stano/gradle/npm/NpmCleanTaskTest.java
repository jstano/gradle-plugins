package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.features.NpmExtensionFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NpmCleanTaskTest extends BasePluginTest {
  @BeforeEach
  void setup() {
    new NpmExtensionFeature().apply(childProject);
  }

  private NpmCleanTask createTask() {
    return childProject.getTasks().register("npmClean", NpmCleanTask.class, childProject).get();
  }

  @Test
  void shouldHaveTheBuildGroupAndACleanDescription() {
    NpmCleanTask task = createTask();

    assertEquals("build", task.getGroup());
    assertEquals("Delete the coverage folder", task.getDescription());
  }

  @Test
  void execShouldDeleteTheCoverageFolder() throws IOException {
    File coverageDir = new File(childProject.getProjectDir(), "coverage");
    coverageDir.mkdirs();
    Files.writeString(new File(coverageDir, "lcov.info").toPath(), "coverage data");
    assertTrue(coverageDir.exists());

    createTask().exec();

    assertFalse(coverageDir.exists());
  }

  @Test
  void execShouldNotFailWhenTheCoverageFolderDoesNotExist() {
    assertDoesNotThrow(() -> createTask().exec());
  }
}
