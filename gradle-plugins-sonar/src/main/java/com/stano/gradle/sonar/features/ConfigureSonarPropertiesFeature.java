package com.stano.gradle.sonar.features;

import com.stano.gradle.base.PluginFeature;
import java.util.LinkedHashMap;
import java.util.Map;
import org.gradle.api.Project;
import org.sonarqube.gradle.SonarExtension;
import org.sonarqube.gradle.SonarQubePlugin;

public class ConfigureSonarPropertiesFeature implements PluginFeature {
  private static final String HOST_PROPERTY = "sonar.host.url";
  private static final String HOST_ENVIRONMENT = "SONAR_HOST_URL";
  private static final String TOKEN_PROPERTY = "sonar.token";
  private static final String TOKEN_ENVIRONMENT = "SONAR_TOKEN";

  @Override
  public void apply(Project project) {
    String host = resolve(project, HOST_PROPERTY, HOST_ENVIRONMENT);
    String token = resolve(project, TOKEN_PROPERTY, TOKEN_ENVIRONMENT);

    if (host == null || token == null) {
      project
          .getLogger()
          .warn(
              "[sonar] host/token not configured for '{}'; skipping Sonar analysis. Set {}/{} "
                  + "(or {}/{}).",
              project.getPath(),
              HOST_PROPERTY,
              TOKEN_PROPERTY,
              HOST_ENVIRONMENT,
              TOKEN_ENVIRONMENT);
      return;
    }

    project.getPlugins().apply(SonarQubePlugin.class);
    Map<String, Object> properties = buildProperties(project, host, token);
    project
        .getExtensions()
        .configure(
            SonarExtension.class, sonar -> sonar.properties(props -> props.properties(properties)));
  }

  Map<String, Object> buildProperties(Project project, String host, String token) {
    Map<String, Object> properties = new LinkedHashMap<>();
    properties.put("sonar.host.url", host);
    properties.put("sonar.token", token);
    properties.put("sonar.projectName", project.getName());
    properties.put("sonar.projectKey", project.getGroup() + ":" + project.getName());
    properties.put("sonar.projectVersion", String.valueOf(project.getVersion()));
    return properties;
  }

  private String resolve(Project project, String propertyName, String environmentVariable) {
    Object value = project.findProperty(propertyName);
    return value != null ? value.toString() : System.getenv(environmentVariable);
  }
}
