package com.stano.gradle.npm.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.sonarqube.gradle.SonarExtension;
import org.sonarqube.gradle.SonarProperties;
import org.sonarqube.gradle.SonarQubePlugin;

public class ConfigureSonarFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    if (!isSonarPluginInstalled(project)) {
      return;
    }

    SonarExtension sonarExtension = project.getExtensions().getByType(SonarExtension.class);
    sonarExtension.properties(properties -> configureProperties(project, properties));
  }

  void configureProperties(Project project, SonarProperties properties) {
    if (project.hasProperty("skipSonar")) {
      properties.property("skipProject", true);
    } else {
      properties.property("sonar.sources", "src");
    }
  }

  private boolean isSonarPluginInstalled(Project project) {
    if (project.getPlugins().hasPlugin(SonarQubePlugin.class)) {
      return true;
    }

    if (project.getParent() != null) {
      return isSonarPluginInstalled(project.getParent());
    }

    return false;
  }
}
