package com.stano.gradle.javalibrary.features;

import com.stano.gradle.PluginFeature;
import com.stano.gradle.javamodule.JavaModulePlugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

public class ConfigurePluginsFeature implements PluginFeature {
   @Override
   public void apply(Project project) {
      PluginContainer plugins = project.getPlugins();
      plugins.apply(JavaModulePlugin.class);
      plugins.apply(MavenPublishPlugin.class);
   }
}
