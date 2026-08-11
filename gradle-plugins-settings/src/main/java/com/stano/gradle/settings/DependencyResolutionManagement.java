package com.stano.gradle.settings;

import com.stano.gradle.base.MavenRepositoryCredentials;
import org.gradle.api.initialization.Settings;

public class DependencyResolutionManagement {
  private static final String STANO_MAVEN_URL_PROPERTY = "com.stano.maven.url";
  private static final String STANO_MAVEN_URL_ENVIRONMENT = "STANO_MAVEN_URL";

  public void configureDependencyResolutionManagement(Settings settings) {
    final var properties = settings.getExtensions().getExtraProperties().getProperties();
    final var stanoMavenUrl =
        properties.containsKey(STANO_MAVEN_URL_PROPERTY)
            ? properties.get(STANO_MAVEN_URL_PROPERTY).toString()
            : System.getenv(STANO_MAVEN_URL_ENVIRONMENT);
    settings
        .getDependencyResolutionManagement()
        .repositories(
            repositories -> {
              repositories.mavenLocal();
              repositories.mavenCentral();
              if (stanoMavenUrl != null) {
                repositories.maven(
                    mavenArtifactRepository -> {
                      MavenRepositoryCredentials.configureCredentials(
                          properties, mavenArtifactRepository);
                      mavenArtifactRepository.setName("stano-maven");
                      mavenArtifactRepository.setUrl(stanoMavenUrl);
                    });
              }
            });
  }
}
