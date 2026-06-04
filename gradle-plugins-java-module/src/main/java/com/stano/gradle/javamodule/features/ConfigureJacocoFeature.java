package com.stano.gradle.javamodule.features;

import com.stano.gradle.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class ConfigureJacocoFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    JacocoReport jacocoReport = (JacocoReport)project.getTasks().getByName("jacocoTestReport");
    jacocoReport.dependsOn(project.getTasks().getByName("test"));
    jacocoReport.getReports().getHtml().getRequired().set(true);
    jacocoReport.getReports().getXml().getRequired().set(true);
    jacocoReport.getOutputs().dir("build/test-results");
    jacocoReport.getClassDirectories()
                .setFrom(jacocoReport.getClassDirectories()
                                     .getFiles()
                                     .stream()
                                     .map(file -> project.fileTree(file).exclude("**/generated/**"))
                                     .toList());
    jacocoReport.getExecutionData()
                .setFrom(project.fileTree(project.getLayout().getBuildDirectory()).include("jacoco/test.exec"));
//    jacocoReport.getSourceDirectories()
//                .setFrom(project.getSourceSets().getByName("main").getAllJava().getSrcDirs());

    project.getGradle().projectsEvaluated(new ProjectsEvaluatedAction(project, jacocoReport));

    Task testTask = project.getTasks().getByName("test");
    testTask.finalizedBy("jacocoTestReport");

//      tasks.named<JacocoReport>("jacocoTestReport") {
//        classDirectories.setFrom(files(classDirectories.files.map {
//          fileTree(it).apply {
//            exclude("**/generated/**")
//          }
//        }))
//        sourceDirectories.setFrom(sourceSets.main.get().java.srcDirs)
//        executionData.setFrom(fileTree(project.layout.buildDirectory).apply {
//          include("jacoco/test.exec")
//        })
//      }
  }
}
