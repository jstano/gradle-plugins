package com.stano.gradle.javamodule.features;

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.stano.gradle.PluginFeature;
import org.gradle.api.Project;

import java.net.URL;

public class ConfigureSpotlessFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    project.getPlugins().apply("com.diffplug.spotless");
    project.getExtensions().configure(SpotlessExtension.class, spotless -> {
      spotless.java(java -> {
        URL configFile = ConfigureSpotlessFeature.class.getResource("/eclipse-java-format.xml");
        java.eclipse().configFile(configFile);
        java.removeUnusedImports();
        java.trimTrailingWhitespace();
        java.endWithNewline();
      });
    });
  }
}
