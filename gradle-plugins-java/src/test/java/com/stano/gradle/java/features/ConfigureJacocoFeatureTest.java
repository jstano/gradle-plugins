package com.stano.gradle.java.features;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigureJacocoFeatureTest {
  @Test
  void shouldConfigureJacocoReportForCrossModuleCoverage(@TempDir File tempDir) {
    File rootDir = new File(tempDir, "root");
    rootDir.mkdirs();
    Project rootProject = ProjectBuilder.builder().withName("root").withProjectDir(rootDir).build();
    rootProject.setVersion("1.0.0");

    File childDir = new File(rootDir, "child");
    childDir.mkdirs();
    Project childProject =
        ProjectBuilder.builder()
            .withName("child")
            .withProjectDir(childDir)
            .withParent(rootProject)
            .build();
    childProject.setVersion("1.0.0");

    childProject.getPluginManager().apply("java");
    childProject.getPluginManager().apply("jacoco");
    new ConfigureJacocoFeature().apply(childProject);

    JacocoReport jacocoReport =
        (JacocoReport) childProject.getTasks().getByName("jacocoTestReport");

    assertNotNull(jacocoReport, "jacocoTestReport task should exist");

    assertTrue(
        jacocoReport.getReports().getHtml().getRequired().getOrNull(),
        "HTML reports should be enabled");
    assertTrue(
        jacocoReport.getReports().getXml().getRequired().getOrNull(),
        "XML reports should be enabled");
  }

  @Test
  void shouldExcludeAnnotationProcessorGeneratedClassesFromCoverage(@TempDir File tempDir)
      throws IOException {
    File rootDir = new File(tempDir, "root");
    rootDir.mkdirs();
    Project rootProject = ProjectBuilder.builder().withName("root").withProjectDir(rootDir).build();
    rootProject.setVersion("1.0.0");

    File childDir = new File(rootDir, "child");
    childDir.mkdirs();
    Project childProject =
        ProjectBuilder.builder()
            .withName("child")
            .withProjectDir(childDir)
            .withParent(rootProject)
            .build();
    childProject.setVersion("1.0.0");

    childProject.getPluginManager().apply("java");
    childProject.getPluginManager().apply("jacoco");

    // Simulate annotation processor generating a source file
    var generatedSrcDir =
        childProject
            .getLayout()
            .getBuildDirectory()
            .dir("generated/sources/annotationProcessor/java/main/com/example")
            .get()
            .getAsFile();
    generatedSrcDir.mkdirs();
    Files.writeString(
        new File(generatedSrcDir, "PersonResponseMapperImpl.java").toPath(),
        "package com.example; public class PersonResponseMapperImpl {}");

    var feature = new ConfigureJacocoFeature();
    var exclusions = feature.buildExclusions(childProject);

    assertTrue(
        exclusions.contains("com/example/PersonResponseMapperImpl.class"),
        "Should exclude the generated main class");
    assertTrue(
        exclusions.contains("com/example/PersonResponseMapperImpl$*.class"),
        "Should exclude generated inner classes");
  }

  @Test
  void shouldAlwaysExcludeGeneratedPathPattern(@TempDir File tempDir) {
    File rootDir = new File(tempDir, "root");
    rootDir.mkdirs();
    Project rootProject = ProjectBuilder.builder().withName("root").withProjectDir(rootDir).build();
    rootProject.setVersion("1.0.0");

    File childDir = new File(rootDir, "child");
    childDir.mkdirs();
    Project childProject =
        ProjectBuilder.builder()
            .withName("child")
            .withProjectDir(childDir)
            .withParent(rootProject)
            .build();
    childProject.setVersion("1.0.0");

    childProject.getPluginManager().apply("java");
    childProject.getPluginManager().apply("jacoco");

    var exclusions = new ConfigureJacocoFeature().buildExclusions(childProject);

    assertTrue(exclusions.contains("**/generated/**"), "Should always exclude **/generated/**");
    assertFalse(
        exclusions.stream().anyMatch(p -> p.contains("PersonResponseMapper")),
        "Should not exclude hand-written source when no annotation processor output exists");
  }
}
