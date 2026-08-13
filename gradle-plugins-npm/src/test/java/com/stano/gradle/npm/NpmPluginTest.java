package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.stano.gradle.base.BasePluginTest;
import org.gradle.api.tasks.TaskContainer;
import org.junit.jupiter.api.Test;

class NpmPluginTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheGenericNpmTasksOnly() {
    childProject.getPluginManager().apply("com.stano.npm");

    TaskContainer tasks = childProject.getTasks();
    assertNotNull(tasks.findByName("npmVersion"));
    assertNotNull(tasks.findByName("npmInstall"));
    assertNotNull(tasks.findByName("npmClean"));
    assertNotNull(tasks.findByName("npmRunBuild"));
    assertNotNull(tasks.findByName("npmTest"));
    assertNull(tasks.findByName("npmAssemble"));
  }

  @Test
  void applyingThePluginShouldRegisterTheNpmExtension() {
    childProject.getPluginManager().apply("com.stano.npm");

    assertNotNull(childProject.getExtensions().findByType(NpmExtension.class));
  }
}
