package com.stano.gradle.base.features;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class JacocoAggregateFeature {
  public void apply(Project project) {
    project
        .getTasks()
        .register(
            "jacocoRootReport",
            JacocoReport.class,
            task -> {
              ConfigurableFileCollection sourceFiles = project.files();
              ConfigurableFileCollection classFiles = project.files();
              Map<File, File> classDirToApGenDir = new LinkedHashMap<>();
              project
                  .getSubprojects()
                  .forEach(
                      sp -> {
                        Task testTask = sp.getTasks().findByName("test");
                        if (testTask != null) {
                          task.dependsOn(testTask);
                          Task jacocoTestReport = sp.getTasks().findByName("jacocoTestReport");
                          boolean jacocoEnabled =
                              jacocoTestReport != null && jacocoTestReport.getEnabled();
                          if (jacocoEnabled) {
                            task.dependsOn(jacocoTestReport);
                          }
                          Task spotlessJava = sp.getTasks().findByName("spotlessJava");
                          if (spotlessJava != null) {
                            task.dependsOn(spotlessJava);
                          }
                          Task copyOtelJavaagent = sp.getTasks().findByName("copyOtelJavaagent");
                          if (copyOtelJavaagent != null) {
                            task.dependsOn(copyOtelJavaagent);
                          }
                          SourceSetContainer sourceSets =
                              sp.getExtensions().findByType(SourceSetContainer.class);
                          if (sourceSets != null) {
                            SourceSet main = sourceSets.findByName("main");
                            if (main != null && jacocoEnabled) {
                              sourceFiles.from(main.getAllSource().getSrcDirs());
                              for (File classDir : main.getOutput().getClassesDirs()) {
                                classFiles.from(classDir);
                                File apGenDir =
                                    sp.getLayout()
                                        .getBuildDirectory()
                                        .dir("generated/sources/annotationProcessor/java/main")
                                        .get()
                                        .getAsFile();
                                classDirToApGenDir.put(classDir, apGenDir);
                              }
                            }
                          }
                        }
                      });
              var execFiles =
                  project.fileTree(project.getRootDir()).include("**/build/jacoco/*.exec");
              task.getExecutionData().setFrom(execFiles);
              task.getSourceDirectories().setFrom(sourceFiles);
              task.getClassDirectories()
                  .setFrom(
                      project.files(
                          (Callable<FileCollection>)
                              () -> {
                                var filtered = project.files();
                                classFiles
                                    .getFiles()
                                    .forEach(
                                        file -> {
                                          File apGenDir = classDirToApGenDir.get(file);
                                          if (apGenDir != null) {
                                            filtered.from(
                                                project
                                                    .fileTree(file)
                                                    .exclude("**/generated/**")
                                                    .exclude(
                                                        element -> {
                                                          String src =
                                                              element
                                                                  .getRelativePath()
                                                                  .getPathString()
                                                                  .replaceAll(
                                                                      "\\$[^/]+\\.class$", ".class")
                                                                  .replace(".class", ".java");
                                                          return new File(apGenDir, src).exists();
                                                        }));
                                          } else {
                                            filtered.from(
                                                project.fileTree(file).exclude("**/generated/**"));
                                          }
                                        });
                                return filtered;
                              }));
              task.getReports().getXml().getRequired().set(true);
              task.getReports().getHtml().getRequired().set(true);
            });
  }
}
