package com.stano.gradle.java.features;

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;

public class ConfigureSpotlessFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    project.getPlugins().apply("com.diffplug.spotless");
    project
        .getExtensions()
        .configure(
            SpotlessExtension.class,
            spotless -> {
              spotless.java(
                  java -> {
                    java.googleJavaFormat("1.35.0").reflowLongStrings().formatJavadoc(true);
                    java.endWithNewline();
                    java.expandWildcardImports();
                    java.importOrder();
                    java.removeUnusedImports();
                    java.trimTrailingWhitespace();
                  });
            });
    project.getTasks().named("check").configure(task -> task.dependsOn("spotlessCheck"));
  }
}
