package com.stano.gradle.java.features;

import com.stano.gradle.base.PluginFeature;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    Set<File> classDirectories = jacocoReport.getClassDirectories().getFiles();
    jacocoReport
        .getClassDirectories()
        .setFrom(
            project.files(
                (Callable<FileCollection>)
                    () -> {
                      var filtered = project.files();
                      var exclusions = buildExclusions(project);
                      for (File file : classDirectories) {
                        filtered.from(project.fileTree(file).exclude(exclusions));
                      }
                      return filtered;
                    }));
    jacocoReport
        .getExecutionData()
        .setFrom(project.getLayout().getBuildDirectory().file("jacoco/test.exec"));
    Task testTask = project.getTasks().getByName("test");
    testTask.finalizedBy("jacocoTestReport");
  }

  List<String> buildExclusions(Project project) {
    var exclusions = new ArrayList<String>();
    exclusions.add("**/generated/**");

    var generatedSrcDir =
        project
            .getLayout()
            .getBuildDirectory()
            .dir("generated/sources/annotationProcessor/java/main")
            .get()
            .getAsFile();
    if (!generatedSrcDir.exists()) {
      return exclusions;
    }

    project
        .fileTree(generatedSrcDir)
        .matching(p -> p.include("**/*.java"))
        .forEach(
            javaFile -> {
              var rel =
                  generatedSrcDir
                      .toPath()
                      .relativize(javaFile.toPath())
                      .toString()
                      .replace(File.separator, "/");
              var base = rel.replace(".java", "");
              exclusions.add(base + ".class");
              exclusions.add(base + "$*.class");
            });

    return exclusions;
  }
}
