package com.stano.gradle.sonar;

import com.stano.gradle.sonar.features.ConfigureGeneratedSourcesFeature;
import com.stano.gradle.sonar.features.ConfigureSonarPropertiesFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.sonarqube.gradle.SonarExtension;

class SonarPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    new ConfigureSonarPropertiesFeature().apply(project);
    new ConfigureGeneratedSourcesFeature().apply(project);

    SonarExtension sonarExtension = project.getExtensions().findByType(SonarExtension.class);
    if (sonarExtension == null) {
      return;
    }
    sonarExtension.properties(
        props ->
            project
                .getProperties()
                .forEach(
                    (key, value) -> {
                      if (key.startsWith("sonar.") && value instanceof String) {
                        props.property(key, value);
                      }
                    }));
  }
}
