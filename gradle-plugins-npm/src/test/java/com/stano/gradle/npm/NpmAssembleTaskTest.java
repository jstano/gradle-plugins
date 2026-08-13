package com.stano.gradle.npm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.npm.features.NpmExtensionFeature;
import com.stano.gradle.npm.features.NpmResourcesExtensionFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

class NpmAssembleTaskTest extends BasePluginTest {
  private void writePackageJson(String contents) throws IOException {
    Files.writeString(new File(childProject.getProjectDir(), "package.json").toPath(), contents);
  }

  private void applyExtensions() {
    new NpmExtensionFeature().apply(childProject);
    new NpmResourcesExtensionFeature().apply(childProject);
  }

  private NpmAssembleTask createTask() {
    return childProject
        .getTasks()
        .register("npmAssemble", NpmAssembleTask.class, childProject)
        .get();
  }

  @Test
  void destinationDirShouldBeComputedFromAssembleOutputPathAndPackageName() throws IOException {
    BaseExtension baseExtension = rootProject.getExtensions().getByType(BaseExtension.class);
    baseExtension.setContextName("my-context");
    writePackageJson("{\"name\": \"my-frontend\"}");
    applyExtensions();

    NpmAssembleTask task = createTask();

    File expected =
        new File(
            childProject.getLayout().getBuildDirectory().getAsFile().get(),
            "resources/main/public/my-context/my-frontend");
    assertEquals(expected, task.getDestinationDir());
  }

  @Test
  void destinationDirShouldUseResourceOutputPathWhenSet() throws IOException {
    writePackageJson("{\"name\": \"my-frontend\"}");
    childProject.getExtensions().getExtraProperties().set("npmResourceOutputPath", "custom/output");
    applyExtensions();

    NpmAssembleTask task = createTask();

    File expected =
        new File(childProject.getLayout().getBuildDirectory().getAsFile().get(), "custom/output");
    assertEquals(expected, task.getDestinationDir());
  }

  @Test
  void shouldThrowWhenPackageJsonDoesNotExist() {
    applyExtensions();

    // Task construction failures are wrapped by Gradle in nested TaskCreationException /
    // TaskInstantiationException layers; the original GradleException with the descriptive
    // message is two levels down.
    Exception exception = assertThrows(Exception.class, this::createTask);
    assertTrue(exception.getCause().getCause().getMessage().contains("package.json"));
  }

  @Test
  void shouldThrowWhenPackageJsonHasNoNameField() throws IOException {
    writePackageJson("{}");
    applyExtensions();

    assertThrows(GradleException.class, this::createTask);
  }
}
