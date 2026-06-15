package com.stano.gradle.java.features;

import com.stano.gradle.base.PluginFeature;
import java.util.concurrent.Callable;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class ConfigureJacocoFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    JacocoReport jacocoReport = (JacocoReport) project.getTasks().getByName("jacocoTestReport");
    jacocoReport.dependsOn(project.getTasks().getByName("test"));
    jacocoReport.getReports().getHtml().getRequired().set(true);
    jacocoReport.getReports().getXml().getRequired().set(true);
    jacocoReport
        .getClassDirectories()
        .setFrom(
            project.files(
                (Callable<FileCollection>)
                    () -> {
                      var filtered = project.files();
                      jacocoReport
                          .getClassDirectories()
                          .getFiles()
                          .forEach(
                              file ->
                                  filtered.from(project.fileTree(file).exclude("**/generated/**")));
                      return filtered;
                    }));
    jacocoReport
        .getExecutionData()
        .setFrom(
            project
                .getRootProject()
                .fileTree(project.getRootProject().getRootDir())
                .include("**/build/jacoco/*.exec"));
    Task testTask = project.getTasks().getByName("test");
    testTask.finalizedBy("jacocoTestReport");
  }
}
