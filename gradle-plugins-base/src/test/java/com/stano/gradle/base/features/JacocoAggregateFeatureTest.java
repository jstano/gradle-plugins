package com.stano.gradle.base.features;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JacocoAggregateFeatureTest {
  @Test
  void shouldRegisterJacocoRootReportTask(@TempDir File tempDir) {
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

    new BaseExtensionFeature().apply(rootProject);
    new JacocoAggregateFeature().apply(rootProject);

    JacocoReport jacocoRootReport =
        (JacocoReport) rootProject.getTasks().getByName("jacocoRootReport");

    assertNotNull(jacocoRootReport, "jacocoRootReport task should be registered");

    // Verify that HTML and XML reports are enabled
    assertTrue(
        jacocoRootReport.getReports().getHtml().getRequired().getOrNull(),
        "HTML reports should be enabled");
    assertTrue(
        jacocoRootReport.getReports().getXml().getRequired().getOrNull(),
        "XML reports should be enabled");
  }
}
