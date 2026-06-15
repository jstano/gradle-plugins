package com.stano.gradle.javalibrary.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.java.JavaPlugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

public class ConfigurePluginsFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    PluginContainer plugins = project.getPlugins();
    plugins.apply(JavaPlugin.class);
    plugins.apply(MavenPublishPlugin.class);
  }
}
