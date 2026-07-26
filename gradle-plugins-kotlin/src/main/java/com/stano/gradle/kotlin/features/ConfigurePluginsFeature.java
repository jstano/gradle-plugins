package com.stano.gradle.kotlin.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.java.JavaPlugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;

public class ConfigurePluginsFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    PluginContainer plugins = project.getPlugins();
    plugins.apply(JavaPlugin.class);
    plugins.apply("org.jetbrains.kotlin.jvm");
  }
}
