package com.stano.gradle.application;

import com.stano.gradle.ProjectVersionProvider;
import com.stano.gradle.RootExtension;
import com.stano.gradle.project.ProjectPlugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.testing.jacoco.tasks.JacocoReport;

import java.util.Objects;

public class ApplicationPlugin extends ProjectPlugin {
  @Override
  public void apply(Project project) {
    super.apply(project);

    project.getPluginManager().apply("base");
    project.getPluginManager().apply("jacoco");

    setVersion(project);
    registerJacocoReport(project);
  }

  private void setVersion(Project project) {
    RootExtension rootExtension = project.getExtensions().getByType(RootExtension.class);
    project.setVersion(new ProjectVersionProvider(project, rootExtension));
    project.getSubprojects().forEach(subProject -> {
      subProject.setVersion(project.getVersion());
    });
  }

  private void registerJacocoReport(Project project) {
    project.getTasks().register("jacocoRootReport", JacocoReport.class, task -> {
      project.getSubprojects().forEach(subProject ->
        task.dependsOn(subProject.getTasks().named("test"))
      );
      project.getSubprojects().forEach(subProject -> {
        var integrationTest = subProject.getTasks().findByName("integrationTest");
        if (integrationTest != null) {
          task.dependsOn(integrationTest);
        }
      });
      project.getSubprojects().forEach(subProject -> {
        var jacocoTestReport = subProject.getTasks().findByName("jacocoTestReport");
        if (jacocoTestReport != null) {
          task.dependsOn(jacocoTestReport);
        }
      });
      project.getSubprojects().forEach(subProject -> {
        var generateSwagger = subProject.getTasks().findByName("generateSwagger");
        if (generateSwagger != null) {
          task.dependsOn(generateSwagger);
        }
      });

      var execFiles = project.fileTree(project.getRootDir()).include("**/build/jacoco/*.exec");
      task.getExecutionData().setFrom(execFiles);

      var sourceFiles = project.files(
        project.getSubprojects().stream()
          .map(sp -> {
            try {
              SourceSetContainer sourceSets = sp.getExtensions().getByType(SourceSetContainer.class);
              return sourceSets.getByName("main").getAllSource().getSrcDirs();
            } catch (Exception e) {
              return null;
            }
          })
          .filter(Objects::nonNull)
          .toArray()
      );
      var classFiles = project.files(
        project.getSubprojects().stream()
          .map(sp -> {
            try {
              SourceSetContainer sourceSets = sp.getExtensions().getByType(SourceSetContainer.class);
              return sourceSets.getByName("main").getOutput();
            } catch (Exception e) {
              return null;
            }
          })
          .filter(Objects::nonNull)
          .toArray()
      );
      task.getSourceDirectories().setFrom(sourceFiles);
      task.getClassDirectories().setFrom(classFiles);

      task.getReports().getXml().getRequired().set(true);
      task.getReports().getHtml().getRequired().set(true);
    });
  }
}
