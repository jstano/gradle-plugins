package com.stano.gradle;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class GradlePluginUtilTest {
  private String oldOsName;

  @BeforeEach
  void setup() {
    oldOsName = System.getProperty("os.name");
  }

  @Test
  void installResourceShouldWork() throws IOException {
    String resource = "testfile.txt";
    File outputFolder = Files.createTempDirectory(null).toFile();
    File outputFile = new File(outputFolder, resource);
    GradlePluginUtil.installResource(getClass().getClassLoader(), outputFolder, resource);
    assertTrue(outputFile.exists());
    outputFile.delete();
    outputFolder.delete();
  }

  @Test
  void isWindowsShouldReturnTrueIfOsNameIsWindows() {
    System.setProperty("os.name", "windows");
    assertTrue(GradlePluginUtil.isWindows());
    System.setProperty("os.name", oldOsName);
  }

  @Test
  void getPropertyShouldWork() {
    var rootProject = ProjectBuilder.builder().build();
    rootProject.getExtensions().getExtraProperties().set("customProperty", "ROOT");
    var childProject = ProjectBuilder.builder().withParent(rootProject).build();
    childProject.getExtensions().getExtraProperties().set("customProperty", "CHILD");
    var subProject = ProjectBuilder.builder().withParent(childProject).build();
    subProject.getExtensions().getExtraProperties().set("customProperty", "SUB");
    assertEquals("ROOT", GradlePluginUtil.getProperty(rootProject, "customProperty"));
    assertEquals("CHILD", GradlePluginUtil.getProperty(childProject, "customProperty"));
    assertEquals("SUB", GradlePluginUtil.getProperty(subProject, "customProperty"));
    assertEquals(
        "java.io.File",
        GradlePluginUtil.getProperty(rootProject, "projectDir").getClass().getName());
    assertEquals(
        "java.io.File",
        GradlePluginUtil.getProperty(childProject, "projectDir").getClass().getName());
    assertEquals(
        "java.io.File",
        GradlePluginUtil.getProperty(subProject, "projectDir").getClass().getName());
    assertNull(GradlePluginUtil.getProperty(rootProject, "notFoundProperty"));
    assertNull(GradlePluginUtil.getProperty(childProject, "notFoundProperty"));
    assertNull(GradlePluginUtil.getProperty(subProject, "notFoundProperty"));
  }

  @Test
  void isRunningInsideIdeShouldReturnFalseIfPropertyNotDefined() {
    System.clearProperty("idea.active");
    assertFalse(GradlePluginUtil.isRunningInsideIde());
  }

  @ParameterizedTest
  @CsvSource({"false, false", "abc, false", "true, true"})
  void isRunningInsideIdeShouldReturnValueBasedOnProperty(
      String ideaActive, boolean expectedResult) {
    System.setProperty("idea.active", ideaActive);
    assertEquals(expectedResult, GradlePluginUtil.isRunningInsideIde());
    System.clearProperty("idea.active");
  }

  @Test
  void notRunningInsideIdeShouldReturnTrueIfPropertyNotDefined() {
    System.clearProperty("idea.active");
    assertTrue(GradlePluginUtil.notRunningInsideIde());
  }

  @ParameterizedTest
  @CsvSource({"false, true", "abc, true", "true, false"})
  void notRunningInsideIdeShouldReturnValueBasedOnProperty(
      String ideaActive, boolean expectedResult) {
    System.setProperty("idea.active", ideaActive);
    assertEquals(expectedResult, GradlePluginUtil.notRunningInsideIde());
    System.clearProperty("idea.active");
  }

  @Test
  void getProjectPropertyShouldWork() throws IOException {
    var rootProject =
        ProjectBuilder.builder()
            .withProjectDir(Files.createTempDirectory("gradle-plugin-util-root").toFile())
            .build();
    rootProject.getExtensions().getExtraProperties().set("outputPath", "/root");
    rootProject.getProjectDir().mkdirs();
    var childProject =
        ProjectBuilder.builder()
            .withParent(rootProject)
            .withProjectDir(new File(rootProject.getProjectDir(), "child"))
            .build();
    childProject.getExtensions().getExtraProperties().set("outputPath", "/child");
    childProject.getProjectDir().mkdirs();
    var subProject =
        ProjectBuilder.builder()
            .withParent(childProject)
            .withProjectDir(new File(childProject.getProjectDir(), "sub"))
            .build();
    subProject.getProjectDir().mkdirs();
    assertEquals("/root", GradlePluginUtil.getProjectProperty(rootProject, "outputPath"));
    assertEquals("/child", GradlePluginUtil.getProjectProperty(childProject, "outputPath"));
    assertEquals("/child", GradlePluginUtil.getProjectProperty(subProject, "outputPath"));
    assertNull(GradlePluginUtil.getProjectProperty(rootProject, "noDefaultValue"));
    assertEquals(
        "the-default-value",
        GradlePluginUtil.getProjectProperty(rootProject, "withDefaultValue", "the-default-value"));
    deleteRecursive(rootProject.getProjectDir().toPath());
  }

  @Test
  void getProjectOrSystemPropertyShouldWork() throws IOException {
    System.setProperty("custom.property", "custom-property-value");
    var rootProject =
        ProjectBuilder.builder()
            .withProjectDir(Files.createTempDirectory("gradle-plugin-util-root").toFile())
            .build();
    rootProject.getExtensions().getExtraProperties().set("outputPath", "/root");
    rootProject.getProjectDir().mkdirs();
    var childProject =
        ProjectBuilder.builder()
            .withParent(rootProject)
            .withProjectDir(new File(rootProject.getProjectDir(), "child"))
            .build();
    childProject.getExtensions().getExtraProperties().set("outputPath", "/child");
    childProject.getProjectDir().mkdirs();
    assertEquals("/root", GradlePluginUtil.getProjectOrSystemProperty(rootProject, "outputPath"));
    assertEquals("/child", GradlePluginUtil.getProjectOrSystemProperty(childProject, "outputPath"));
    assertNull(GradlePluginUtil.getProjectOrSystemProperty(rootProject, "noDefaultValue"));
    assertEquals(
        "the-default-value",
        GradlePluginUtil.getProjectOrSystemProperty(
            rootProject, "withDefaultValue", "the-default-value"));
    assertEquals(
        "custom-property-value",
        GradlePluginUtil.getProjectOrSystemProperty(rootProject, "custom.property"));
    deleteRecursive(rootProject.getProjectDir().toPath());
  }

  @Test
  void getNestedProjectNameShouldWork() throws IOException {
    var rootProject =
        ProjectBuilder.builder()
            .withName("Gradle-Root-Project")
            .withProjectDir(Files.createTempDirectory("gradle-plugin-util-root").toFile())
            .build();
    rootProject.getProjectDir().mkdirs();
    var childProject =
        ProjectBuilder.builder()
            .withParent(rootProject)
            .withName("Gradle-Child-Project")
            .withProjectDir(new File(rootProject.getProjectDir(), "child"))
            .build();
    childProject.getProjectDir().mkdirs();
    assertEquals("gradle-root-project", GradlePluginUtil.getNestedProjectName(rootProject));
    assertEquals(
        "gradle-root-project-gradle-child-project",
        GradlePluginUtil.getNestedProjectName(childProject));
    deleteRecursive(rootProject.getProjectDir().toPath());
  }

  private void deleteRecursive(java.nio.file.Path path) throws IOException {
    Files.walk(path)
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
