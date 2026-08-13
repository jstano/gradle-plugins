package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.features.NpmExtensionFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NpmBuildTaskTest extends BasePluginTest {
  private ExecOperations execOperations;

  @BeforeEach
  void setup() throws IOException {
    Files.writeString(
        new File(childProject.getProjectDir(), "package.json").toPath(),
        "{\"name\": \"my-frontend\"}");
    Files.writeString(new File(childProject.getProjectDir(), "package-lock.json").toPath(), "{}");
    new NpmExtensionFeature().apply(childProject);

    execOperations = mock(ExecOperations.class);
    ExecResult execResult = mock(ExecResult.class);
    when(execResult.getExitValue()).thenReturn(0);
    when(execOperations.exec(any())).thenReturn(execResult);
  }

  private NpmBuildTask createTask() {
    return childProject
        .getTasks()
        .register("npmRunBuild", NpmBuildTask.class, childProject, execOperations)
        .get();
  }

  @Test
  void shouldHaveTheBuildGroupAndABuildDescription() {
    NpmBuildTask task = createTask();

    assertEquals("build", task.getGroup());
    assertEquals("Build the package using npm", task.getDescription());
  }

  @Test
  void inputFilesShouldOnlyContainRequiredFilesWhenOptionalFilesAreMissing() {
    NpmBuildTask task = createTask();

    assertEquals(2, task.getInputFiles().size());
    assertNull(task.getPublicDirectory());
  }

  @Test
  void inputFilesShouldIncludeAllOptionalFilesWhenPresent() throws IOException {
    File projectDir = childProject.getProjectDir();
    Files.writeString(new File(projectDir, "config-overrides.js").toPath(), "");
    Files.writeString(new File(projectDir, "tsconfig.json").toPath(), "{}");
    Files.writeString(new File(projectDir, "tsconfig.test.json").toPath(), "{}");
    Files.writeString(new File(projectDir, "tslint.json").toPath(), "{}");
    new File(projectDir, "public").mkdirs();

    NpmBuildTask task = createTask();

    assertEquals(6, task.getInputFiles().size());
    assertNotNull(task.getPublicDirectory());
  }

  @Test
  void resourceFilesDirectoryShouldBeNullWhenNotConfigured() {
    NpmBuildTask task = createTask();

    assertNull(task.getResourceFilesDirectory());
  }

  @Test
  void resourceFilesDirectoryShouldBeSetWhenConfigured() {
    NpmExtension npmExtension = childProject.getExtensions().getByType(NpmExtension.class);
    npmExtension.getResourceFilesDirectory().set("some-resources");

    NpmBuildTask task = createTask();

    assertNotNull(task.getResourceFilesDirectory());
  }

  @Test
  void outputDirectoryShouldBeUnderTheDistFolder() {
    NpmBuildTask task = createTask();

    assertEquals("dist", task.getOutputDirectory().getName());
  }
}
