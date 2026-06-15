package com.stano.gradle.java.features;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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

    // Apply java plugin first to create test task, then jacoco
    childProject.getPluginManager().apply("java");
    childProject.getPluginManager().apply("jacoco");
    new ConfigureJacocoFeature().apply(childProject);

    JacocoReport jacocoReport =
        (JacocoReport) childProject.getTasks().getByName("jacocoTestReport");

    assertNotNull(jacocoReport, "jacocoTestReport task should exist");

    // Verify reports are enabled
    assertTrue(
        jacocoReport.getReports().getHtml().getRequired().getOrNull(),
        "HTML reports should be enabled");
    assertTrue(
        jacocoReport.getReports().getXml().getRequired().getOrNull(),
        "XML reports should be enabled");
  }
}
