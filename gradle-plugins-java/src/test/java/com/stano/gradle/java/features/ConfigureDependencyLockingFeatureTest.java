package com.stano.gradle.java.features;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BaseExtension;
import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.LockMode;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigureDependencyLockingFeatureTest {
  @Test
  void whenEnabledShouldLockAllConfigurationsInStrictMode(@TempDir File tempDir) {
    Project childProject = childProjectWithDependencyLocking(tempDir, true);

    new ConfigureDependencyLockingFeature().apply(childProject);

    assertTrue(
        childProject.getDependencyLocking().getLockMode().get() == LockMode.STRICT,
        "Lock mode should be STRICT");
  }

  @Test
  void whenDisabledShouldNotConfigureLocking(@TempDir File tempDir) {
    Project childProject = childProjectWithDependencyLocking(tempDir, false);

    new ConfigureDependencyLockingFeature().apply(childProject);

    assertFalse(
        childProject.getDependencyLocking().getLockMode().get() == LockMode.STRICT,
        "Lock mode should not be changed when dependency locking is disabled");
  }

  @Test
  void whenNotSetByAnyPluginShouldNotConfigureLocking(@TempDir File tempDir) {
    Project childProject = childProjectWithDependencyLocking(tempDir, null);

    new ConfigureDependencyLockingFeature().apply(childProject);

    assertFalse(
        childProject.getDependencyLocking().getLockMode().get() == LockMode.STRICT,
        "Lock mode should not be changed when no plugin has set a dependency locking default");
  }

  private Project childProjectWithDependencyLocking(File tempDir, Boolean dependencyLocking) {
    File rootDir = new File(tempDir, "root");
    rootDir.mkdirs();
    Project rootProject = ProjectBuilder.builder().withName("root").withProjectDir(rootDir).build();
    rootProject.setVersion("1.0.0");

    BaseExtension baseExtension = new BaseExtension();
    baseExtension.setDependencyLocking(dependencyLocking);
    rootProject.getExtensions().add("root", baseExtension);

    File childDir = new File(rootDir, "child");
    childDir.mkdirs();
    Project childProject =
        ProjectBuilder.builder()
            .withName("child")
            .withProjectDir(childDir)
            .withParent(rootProject)
            .build();
    childProject.setVersion("1.0.0");
    return childProject;
  }
}
