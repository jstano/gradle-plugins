package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.tasks.TaskContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NpmResourcesPluginTest extends BasePluginTest {
  @BeforeEach
  void writePackageJson() throws IOException {
    Files.writeString(
        new File(childProject.getProjectDir(), "package.json").toPath(),
        "{\"name\": \"my-frontend\"}");
  }

  @Test
  void applyingThePluginShouldAutoApplyTheGenericNpmPlugin() {
    childProject.getPluginManager().apply("com.stano.npm-resources");

    assertTrue(childProject.getPluginManager().hasPlugin("com.stano.npm"));
  }

  @Test
  void applyingThePluginShouldRegisterTheNpmAssembleTask() {
    childProject.getPluginManager().apply("com.stano.npm-resources");

    TaskContainer tasks = childProject.getTasks();
    assertNotNull(tasks.findByName("npmAssemble"));
    // The generic tasks are also present, via the auto-applied com.stano.npm.
    assertNotNull(tasks.findByName("npmRunBuild"));
    assertNotNull(tasks.findByName("npmTest"));
  }

  @Test
  void applyingThePluginShouldRegisterTheNpmResourcesExtension() {
    childProject.getPluginManager().apply("com.stano.npm-resources");

    assertNotNull(childProject.getExtensions().findByType(NpmResourcesExtension.class));
  }
}
