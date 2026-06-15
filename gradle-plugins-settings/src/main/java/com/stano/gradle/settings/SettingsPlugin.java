package com.stano.gradle.settings;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

public class SettingsPlugin implements Plugin<Settings> {
  @Override
  public void apply(Settings settings) {
    settings.getExtensions().create("buildCacheSettings", BuildCacheSettingsExtension.class);
    settings
        .getGradle()
        .settingsEvaluated(
            s -> {
              new DependencyResolutionManagement()
                  .configureDependencyResolutionManagement(settings);
              new BuildCache().configureBuildCache(settings);
              String stanoVersion = readStanoVersion();
              var plugins = settings.getPluginManagement().getPlugins();
              plugins.id("org.jetbrains.kotlin.jvm").version("2.4.0");
              for (String id :
                  List.of(
                      "com.stano.base",
                      "com.stano.application",
                      "com.stano.library",
                      "com.stano.java",
                      "com.stano.java-library",
                      "com.stano.spring-boot",
                      "com.stano.sonar",
                      "com.stano.docker",
                      "com.stano.docker-compose",
                      "com.stano.docker-run")) {
                plugins.id(id).version(stanoVersion);
              }
              settings.getPluginManagement().getRepositories().gradlePluginPortal();
            });
  }

  private String readStanoVersion() {
    try (var stream = getClass().getResourceAsStream("stano-plugins.properties")) {
      var props = new Properties();
      props.load(stream);
      return props.getProperty("version");
    } catch (IOException e) {
      throw new RuntimeException("Failed to read stano plugins version", e);
    }
  }
}
