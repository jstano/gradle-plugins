package com.stano.gradle.javalibrary.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.tasks.Jar;

public class ConfigureArtifactsFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    JavaPluginExtension javaPluginExtension =
        project.getExtensions().getByType(JavaPluginExtension.class);
    javaPluginExtension.withJavadocJar();
    javaPluginExtension.withSourcesJar();
    Jar sourcesJarTask = (Jar) project.getTasks().findByName("sourcesJar");
    Jar javadocJarTask = (Jar) project.getTasks().findByName("javadocJar");
    if (project.hasProperty("artifactIdPrefix")) {
      String artifactIdPrefix = (String) project.findProperty("artifactIdPrefix");
      if (sourcesJarTask != null) {
        sourcesJarTask.getArchiveBaseName().set(artifactIdPrefix + "-" + project.getName());
      }
      if (javadocJarTask != null) {
        javadocJarTask.getArchiveBaseName().set(artifactIdPrefix + "-" + project.getName());
      }
    }
    if (sourcesJarTask != null) {
      sourcesJarTask.exclude("**/.gitkeep");
    }
    if (javadocJarTask != null) {
      javadocJarTask.exclude("**/.gitkeep");
    }
  }
}
