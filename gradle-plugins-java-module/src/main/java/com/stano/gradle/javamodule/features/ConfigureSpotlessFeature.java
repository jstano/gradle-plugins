package com.stano.gradle.javamodule.features;

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.stano.gradle.PluginFeature;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
                    File configFile = extractEclipseConfigFile();
                    if (configFile.canRead()) {
                      java.googleJavaFormat();
                      //                      java.eclipse().configFile(configFile);
                      java.removeUnusedImports();
                      java.expandWildcardImports();
                      java.trimTrailingWhitespace();
                      java.endWithNewline();
                    }
                  });
            });
    project.getTasks().named("check").configure(task -> task.dependsOn("spotlessCheck"));
  }

  private int add(
      int aaaaaa,
      int bbbbbbb,
      int ccccccc,
      int dddddddd,
      int eeeeeeee,
      int fffffff,
      int gggggggg,
      int hhhhhhhh) {
    return aaaaaa + bbbbbbb;
  }

  private File extractEclipseConfigFile() {
    try {
      URL resourceUrl = ConfigureSpotlessFeature.class.getResource("/eclipse-java-format.xml");
      if (resourceUrl == null) {
        throw new RuntimeException("eclipse-java-format.xml not found in classpath");
      }
      Path tempFile = Files.createTempFile("eclipse-java-format", ".xml");
      tempFile.toFile().deleteOnExit();
      try (InputStream in = resourceUrl.openStream()) {
        Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
      }
      return tempFile.toFile();
    } catch (IOException e) {
      throw new RuntimeException("Failed to extract eclipse-java-format.xml", e);
    }
  }
}
