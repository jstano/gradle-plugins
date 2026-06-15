package com.stano.gradle.javaconventions.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;

public class ConfigureArtifactsFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    Jar jarTask = (Jar) project.getTasks().getByName("jar");
    jarTask.setZip64(true);
    jarTask.exclude("**/.gitkeep");
  }
}
