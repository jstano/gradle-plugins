package com.stano.gradle.settings;

import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.initialization.Settings;

public class DependencyResolutionManagement {
  private static final String STANO_MAVEN_URL_PROPERTY = "com.stano.maven.url";
  private static final String STANO_MAVEN_URL_ENVIRONMENT = "STANO_MAVEN_URL";
  private static final String STANO_MAVEN_USERNAME_PROPERTY = "com.stano.maven.username";
  private static final String STANO_MAVEN_USERNAME_ENVIRONMENT = "STANO_MAVEN_USERNAME";
  private static final String STANO_MAVEN_PASSWORD_PROPERTY = "com.stano.maven.password";
  private static final String STANO_MAVEN_PASSWORD_ENVIRONMENT = "STANO_MAVEN_PASSWORD";

  public void configureDependencyResolutionManagement(Settings settings) {
    final var properties = settings.getExtensions().getExtraProperties().getProperties();
    final var stanoMavenUrl = properties.containsKey(STANO_MAVEN_URL_PROPERTY)
                              ? properties.get(STANO_MAVEN_URL_PROPERTY).toString()
                              : System.getenv(STANO_MAVEN_URL_ENVIRONMENT);

    settings.getDependencyResolutionManagement().repositories(repositories -> {
      repositories.mavenLocal();
      repositories.mavenCentral();
      repositories.maven(mavenArtifactRepository -> {
        configureCredentials(settings, mavenArtifactRepository);

        mavenArtifactRepository.setName("stano-maven");
        mavenArtifactRepository.setUrl(stanoMavenUrl);
      });
    });
  }

  private void configureCredentials(Settings settings, MavenArtifactRepository mavenArtifactRepository) {
    final var properties = settings.getExtensions().getExtraProperties().getProperties();
    final var stanoMavenUsername = properties.containsKey(STANO_MAVEN_USERNAME_PROPERTY)
                                     ? properties.get(STANO_MAVEN_USERNAME_PROPERTY).toString()
                                     : System.getenv(STANO_MAVEN_USERNAME_ENVIRONMENT);
    final var stanoMavenPassword = properties.containsKey(STANO_MAVEN_PASSWORD_PROPERTY)
                                   ? properties.get(STANO_MAVEN_PASSWORD_PROPERTY).toString()
                                   : System.getenv(STANO_MAVEN_PASSWORD_ENVIRONMENT);
    final var credentials = mavenArtifactRepository.getCredentials(PasswordCredentials.class);
    credentials.setUsername(stanoMavenUsername);
    credentials.setPassword(stanoMavenPassword);
  }
}
