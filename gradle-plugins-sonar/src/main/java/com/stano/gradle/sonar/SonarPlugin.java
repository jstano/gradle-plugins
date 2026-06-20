package com.stano.gradle.sonar;

import com.stano.gradle.sonar.features.ConfigureGeneratedSourcesFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.sonarqube.gradle.SonarExtension;
import org.sonarqube.gradle.SonarQubePlugin;

class SonarPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(SonarQubePlugin.class);
    new ConfigureGeneratedSourcesFeature().apply(project);
    project
        .getExtensions()
        .configure(
            SonarExtension.class,
            sonar ->
                sonar.properties(
                    props ->
                        project
                            .getProperties()
                            .forEach(
                                (key, value) -> {
                                  if (key.startsWith("sonar.") && value instanceof String) {
                                    props.property(key, value);
                                  }
                                })));
  }
}
