package com.stano.gradle.settings;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

public class SettingsPlugin implements Plugin<Settings> {
  @Override
  public void apply(Settings settings) {
    settings.getExtensions().create("buildCacheSettings", BuildCacheSettingsExtension.class);

    settings.getGradle().settingsEvaluated(s -> {
      new DependencyResolutionManagement().configureDependencyResolutionManagement(settings);
      new BuildCache().configureBuildCache(settings);
      settings.getPluginManagement().getPlugins().id("org.jetbrains.kotlin.jvm").version("2.4.0");
      settings.getPluginManagement().getRepositories().gradlePluginPortal();
    });
  }
}
