package com.stano.gradle.javalibrary.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

public class ConfigurePublishFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    if (!project.getPlugins().hasPlugin(MavenPublishPlugin.class)) {
      return;
    }
    MavenRepositoryUtils.configurePublishing(project);
    MavenRepositoryUtils.disableEnforcedPlatformError(project);
  }
}
