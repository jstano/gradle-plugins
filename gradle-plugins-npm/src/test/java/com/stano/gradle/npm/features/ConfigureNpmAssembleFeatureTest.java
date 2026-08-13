package com.stano.gradle.npm.features;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigureNpmAssembleFeatureTest extends BasePluginTest {
  @BeforeEach
  void setup() throws IOException {
    Files.writeString(
        new File(childProject.getProjectDir(), "package.json").toPath(),
        "{\"name\": \"my-frontend\"}");

    new NpmExtensionFeature().apply(childProject);
    new NpmResourcesExtensionFeature().apply(childProject);
    new ConfigureTasksFeature().apply(childProject);
  }

  @Test
  void applyShouldRegisterNpmAssemble() {
    new ConfigureNpmAssembleFeature().apply(childProject);

    assertNotNull(childProject.getTasks().findByName("npmAssemble"));
  }

  @Test
  void npmAssembleShouldDependOnNpmRunBuild() {
    new ConfigureNpmAssembleFeature().apply(childProject);

    TaskContainer tasks = childProject.getTasks();
    Task npmAssembleTask = tasks.getByName("npmAssemble");
    Task npmRunBuildTask = tasks.getByName("npmRunBuild");
    assertTrue(
        npmAssembleTask
            .getTaskDependencies()
            .getDependencies(npmAssembleTask)
            .contains(npmRunBuildTask));
  }

  @Test
  void jarAndTestShouldDependOnNpmAssembleAndNpmTestWhenJavaPluginIsApplied() {
    childProject.getPluginManager().apply(JavaPlugin.class);

    new ConfigureNpmAssembleFeature().apply(childProject);

    TaskContainer tasks = childProject.getTasks();
    Task jarTask = tasks.getByName("jar");
    Task testTask = tasks.getByName("test");
    Task npmAssembleTask = tasks.getByName("npmAssemble");
    Task npmTestTask = tasks.getByName("npmTest");
    assertTrue(jarTask.getTaskDependencies().getDependencies(jarTask).contains(npmAssembleTask));
    assertTrue(testTask.getTaskDependencies().getDependencies(testTask).contains(npmTestTask));
  }

  @Test
  void applyShouldNotFailWhenTheJavaPluginIsNotApplied() {
    new ConfigureNpmAssembleFeature().apply(childProject);

    assertNotNull(childProject.getTasks().findByName("npmAssemble"));
  }

  @Test
  void jarShouldNotDependOnNpmAssembleWhenTheJavaPluginIsNotApplied() {
    new ConfigureNpmAssembleFeature().apply(childProject);

    assertFalse(childProject.getTasks().getNames().contains("jar"));
  }
}
