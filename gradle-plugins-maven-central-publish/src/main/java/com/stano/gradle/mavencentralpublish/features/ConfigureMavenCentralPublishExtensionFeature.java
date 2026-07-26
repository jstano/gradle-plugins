package com.stano.gradle.mavencentralpublish.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.mavencentralpublish.MavenCentralPublishExtension;
import org.gradle.api.Project;

public class ConfigureMavenCentralPublishExtensionFeature implements PluginFeature {
  public static final String EXTENSION_NAME = "mavenCentralPublish";

  @Override
  public void apply(Project project) {
    project.getExtensions().create(EXTENSION_NAME, MavenCentralPublishExtension.class);
  }
}
