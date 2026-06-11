package com.stano.gradle.project.features;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class JacocoAggregateFeature {
  public void apply(Project project) {
    project.getTasks().register("jacocoRootReport", JacocoReport.class, task -> {
      ConfigurableFileCollection sourceFiles = project.files();
      ConfigurableFileCollection classFiles = project.files();

      project.getSubprojects().forEach(sp -> {
        Task testTask = sp.getTasks().findByName("test");
        if (testTask != null) {
          task.dependsOn(testTask);

          Task jacocoTestReport = sp.getTasks().findByName("jacocoTestReport");
          if (jacocoTestReport != null) {
            task.dependsOn(jacocoTestReport);
          }

          SourceSetContainer sourceSets = sp.getExtensions().findByType(SourceSetContainer.class);
          if (sourceSets != null) {
            SourceSet main = sourceSets.findByName("main");
            if (main != null) {
              sourceFiles.from(main.getAllSource().getSrcDirs());
              classFiles.from(main.getOutput());
            }
          }
        }
      });

      var execFiles = project.fileTree(project.getRootDir()).include("**/build/jacoco/*.exec");
      task.getExecutionData().setFrom(execFiles);
      task.getSourceDirectories().setFrom(sourceFiles);
      task.getClassDirectories().setFrom(classFiles);

      task.getReports().getXml().getRequired().set(true);
      task.getReports().getHtml().getRequired().set(true);
    });
  }
}
