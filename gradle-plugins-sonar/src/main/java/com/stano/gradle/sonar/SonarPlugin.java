package com.stano.gradle.sonar;

import com.stano.gradle.base.GradleProperties;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonarqube.gradle.SonarExtension;
import org.sonarqube.gradle.SonarQubePlugin;

class SonarPlugin implements Plugin<Project> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SonarPlugin.class);

  @Override
  public void apply(Project project) {
    final var sonarEnabled = !GradleProperties.booleanProperty(project, "com.stano.sonar.disabled");
    final var sonarHostUrl = GradleProperties.getProperty(project, "com.stano.sonar.host");
    final var sonarToken = GradleProperties.getProperty(project, "com.stano.sonar.token");
    final var sonarFailBuildEnabled =
        GradleProperties.booleanProperty(project, "com.stano.sonar.fail-build-enabled");
    if (sonarEnabled && sonarHostUrl != null && sonarToken != null) {
      final var sonarProjectName = getSonarProjectName(project);
      final var plugins = project.getPlugins();
      plugins.apply(SonarQubePlugin.class);
      SonarExtension sonarExtension = project.getExtensions().getByType(SonarExtension.class);
      sonarExtension.properties(
          properties -> {
            properties.property("sonar.host.url", sonarHostUrl);
            properties.property("sonar.token", sonarToken);
            properties.property("sonar.projectName", sonarProjectName);
            properties.property(
                "sonar.projectKey", String.format("%s:%s", project.getGroup(), sonarProjectName));
            properties.property("sonar.projectVersion", project.getVersion());
            if (sonarFailBuildEnabled) {
              LOGGER.info("********** sonar.qualitygate.wait=true");
              properties.property("sonar.qualitygate.wait", true);
            }
          });
    } else {
      System.out.println("*************************");
      System.out.println("Sonar not enabled");
      System.out.printf("sonarEnabled: %s\n", sonarEnabled);
      System.out.printf("sonarHostUrl: %s\n", sonarHostUrl);
      System.out.printf("sonarToken: %s\n", sonarToken);
      System.out.println("*************************");
    }
  }

  private String getSonarProjectName(Project project) {
    if (project.hasProperty("sonarProjectName")) {
      return (String) project.findProperty("sonarProjectName");
    }
    return project.getName();
  }
}
