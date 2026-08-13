package com.stano.gradle.npm.features;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigureTasksFeatureTest extends BasePluginTest {
  @BeforeEach
  void setup() {
    new NpmExtensionFeature().apply(childProject);
  }

  @Test
  void applyShouldRegisterTheGenericNpmTasksOnly() {
    new ConfigureTasksFeature().apply(childProject);

    TaskContainer tasks = childProject.getTasks();
    assertNotNull(tasks.findByName("npmVersion"));
    assertNotNull(tasks.findByName("npmInstall"));
    assertNotNull(tasks.findByName("npmClean"));
    assertNotNull(tasks.findByName("npmRunBuild"));
    assertNotNull(tasks.findByName("npmTest"));
    // npmAssemble belongs to com.stano.npm-resources, not the generic plugin.
    assertNull(tasks.findByName("npmAssemble"));
  }

  @Test
  void applyShouldNotFailWhenNoBaseOrJavaPluginIsEverApplied() {
    // A pure npm-only project has no "clean" task at all; npmClean must not require one.
    assertDoesNotThrow(() -> new ConfigureTasksFeature().apply(childProject));
    assertNull(childProject.getTasks().findByName("clean"));
  }

  @Test
  void npmCleanShouldBeWiredIntoCleanWhenTheBasePluginIsAppliedFirst() {
    childProject.getPluginManager().apply(BasePlugin.class);

    new ConfigureTasksFeature().apply(childProject);

    assertCleanDependsOnNpmClean();
  }

  @Test
  void npmCleanShouldBeWiredIntoCleanWhenTheBasePluginIsAppliedAfterwards() {
    // Regression test: applying com.stano.npm before java/base must not crash when java/base
    // is applied afterwards (it previously collided with an eagerly-created placeholder task).
    new ConfigureTasksFeature().apply(childProject);

    childProject.getPluginManager().apply(BasePlugin.class);

    assertCleanDependsOnNpmClean();
  }

  private void assertCleanDependsOnNpmClean() {
    TaskContainer tasks = childProject.getTasks();
    Task cleanTask = tasks.getByName("clean");
    Task npmCleanTask = tasks.getByName("npmClean");
    assertTrue(cleanTask.getTaskDependencies().getDependencies(cleanTask).contains(npmCleanTask));
  }

  @Test
  void applyShouldNotWireTestEvenWhenTheJavaPluginIsApplied() {
    childProject.getPluginManager().apply(JavaPlugin.class);

    new ConfigureTasksFeature().apply(childProject);

    TaskContainer tasks = childProject.getTasks();
    Task testTask = tasks.getByName("test");
    Task npmTestTask = tasks.getByName("npmTest");
    assertFalse(testTask.getTaskDependencies().getDependencies(testTask).contains(npmTestTask));
  }
}
