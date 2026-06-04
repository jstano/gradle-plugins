package com.stano.gradle.settings;

import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.credentials.HttpHeaderCredentials;
import org.gradle.api.initialization.Settings;
import org.gradle.authentication.http.HttpHeaderAuthentication;

public class DependencyResolutionManagement {
  private static final String R365_MAVEN_URL_PROPERTY = "com.r365.maven.url";
  private static final String R365_MAVEN_URL_ENVIRONMENT_VARIABLE = "R365_MAVEN_URL";
  private static final String R365_GITLAB_PRIVATE_TOKEN_PROPERTY = "com.r365.gitlab.private-token";
  private static final String R365_MAVEN_DEPLOY_TOKEN = "R365_MAVEN_DEPLOY_TOKEN";

  public void configureDependencyResolutionManagement(Settings settings) {
    final var properties = settings.getExtensions().getExtraProperties().getProperties();
    final var r365MavenUrl = properties.containsKey(R365_MAVEN_URL_PROPERTY)
                             ? properties.get(R365_MAVEN_URL_PROPERTY).toString()
                             : System.getenv(R365_MAVEN_URL_ENVIRONMENT_VARIABLE);

    settings.getDependencyResolutionManagement().repositories(repositories -> {
      repositories.mavenLocal();
      repositories.mavenCentral();
      repositories.maven(mavenArtifactRepository -> {
        configureCredentials(settings, mavenArtifactRepository);

        mavenArtifactRepository.setName("r365-maven");
        mavenArtifactRepository.setUrl(r365MavenUrl);
        mavenArtifactRepository.authentication(auth -> auth.create("header", HttpHeaderAuthentication.class, header -> {
        }));
      });
      repositories.maven(mavenArtifactRepository -> {
        configureCredentials(settings, mavenArtifactRepository);

        mavenArtifactRepository.setName("mef");
        mavenArtifactRepository.setUrl("https://gitlab.com/api/v4/projects/33297279/packages/maven");
        mavenArtifactRepository.authentication(auth -> auth.create("header", HttpHeaderAuthentication.class, header -> {
        }));
      });
    });
  }

  private void configureCredentials(Settings settings, MavenArtifactRepository mavenArtifactRepository) {
    final var properties = settings.getExtensions().getExtraProperties().getProperties();
    final var ciJobToken = System.getenv("CI_JOB_TOKEN");
    final var r365MavenDeployToken = properties.containsKey(R365_GITLAB_PRIVATE_TOKEN_PROPERTY)
                                     ? properties.get(R365_GITLAB_PRIVATE_TOKEN_PROPERTY).toString()
                                     : System.getenv(R365_MAVEN_DEPLOY_TOKEN);
    final var credentials = mavenArtifactRepository.getCredentials(HttpHeaderCredentials.class);
    credentials.setName(ciJobToken != null ? "Job-Token" : "Private-Token");
    credentials.setValue(ciJobToken != null ? ciJobToken : r365MavenDeployToken);
  }
}
