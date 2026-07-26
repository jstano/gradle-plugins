package com.stano.gradle.kotlin.features;

import com.stano.gradle.base.PluginFeature;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Project;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

public class ConfigureCompilersFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    project
        .getTasks()
        .withType(
            KotlinCompile.class,
            kotlinCompile -> {
              kotlinCompile.getCompilerOptions().getFreeCompilerArgs().addAll(getCompileOptions());
              kotlinCompile.setIncremental(true);
            });
  }

  private List<String> getCompileOptions() {
    return Arrays.asList("-Xlint:none", "-Xdoclint:none", "-nowarn", "-parameters");
  }
}
