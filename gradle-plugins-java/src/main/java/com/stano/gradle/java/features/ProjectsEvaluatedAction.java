package com.stano.gradle.java.features;

import java.util.List;
import java.util.stream.Stream;
import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.SourceSetOutput;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class ProjectsEvaluatedAction implements Action<Gradle> {
  private final Project project;
  private final JacocoReport jacocoReport;

  public ProjectsEvaluatedAction(Project project, JacocoReport jacocoReport) {
    this.project = project;
    this.jacocoReport = jacocoReport;
  }

  @Override
  public void execute(Gradle gradle) {
    DomainObjectSet<ProjectDependency> apiDependencies =
        project
            .getConfigurations()
            .getByName("api")
            .getAllDependencies()
            .withType(ProjectDependency.class);
    DomainObjectSet<ProjectDependency> implementationDependencies =
        project
            .getConfigurations()
            .getByName("implementation")
            .getAllDependencies()
            .withType(ProjectDependency.class);
    List<Project> dependentProjects =
        Stream.concat(apiDependencies.stream(), implementationDependencies.stream())
            .map(it -> project.project(it.getPath()))
            .toList();
    dependentProjects.forEach(
        dependentProject -> {
          SourceSetContainer sourceSetContainer =
              dependentProject.getExtensions().findByType(SourceSetContainer.class);
          if (sourceSetContainer != null) {
            sourceSetContainer.stream()
                .filter(it -> it.getName().equals("main"))
                .findFirst()
                .ifPresent(
                    mainSourceSet -> {
                      SourceDirectorySet javaSrc = mainSourceSet.getAllJava();
                      SourceSetOutput mainOutput = mainSourceSet.getOutput();
                      jacocoReport.additionalSourceDirs(project.files(javaSrc.getSrcDirs()));
                      jacocoReport.additionalClassDirs(mainOutput);
                      Task compileJava = dependentProject.getTasks().findByName("compileJava");
                      if (compileJava != null) {
                        jacocoReport.dependsOn(compileJava);
                      }
                      Task compileTestJava =
                          dependentProject.getTasks().findByName("compileTestJava");
                      if (compileTestJava != null) {
                        jacocoReport.dependsOn(compileTestJava);
                      }
                    });
          }
        });

    gradle
        .getRootProject()
        .getSubprojects()
        .forEach(
            sub -> {
              jacocoReport
                  .getExecutionData()
                  .from(sub.getLayout().getBuildDirectory().file("jacoco/test.exec"));
              Task testTask = sub.getTasks().findByName("test");
              if (testTask != null) {
                jacocoReport.dependsOn(testTask);
              }
              Task jacocoSubReport = sub.getTasks().findByName("jacocoTestReport");
              if (jacocoSubReport != null) {
                jacocoReport.dependsOn(jacocoSubReport);
              }
            });
  }
}
