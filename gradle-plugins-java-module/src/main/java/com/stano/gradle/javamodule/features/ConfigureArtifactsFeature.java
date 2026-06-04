package com.stano.gradle.javamodule.features;

import com.stano.gradle.PluginFeature;
import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;

public class ConfigureArtifactsFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    Jar jarTask = (Jar)project.getTasks().getByName("jar");
    jarTask.setZip64(true);
    jarTask.exclude("**/.gitkeep");
  }
}
