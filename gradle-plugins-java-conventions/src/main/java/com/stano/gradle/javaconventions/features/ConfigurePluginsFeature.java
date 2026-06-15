package com.stano.gradle.javaconventions.features;

import com.stano.gradle.base.PluginFeature;
import org.barfuin.gradle.jacocolog.JacocoLogPlugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;

public class ConfigurePluginsFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    PluginContainer plugins = project.getPlugins();
    plugins.apply(JavaLibraryPlugin.class);
    plugins.apply("org.jetbrains.kotlin.jvm");
    plugins.apply(JacocoLogPlugin.class);
    plugins.apply(JacocoPlugin.class);
  }
}
