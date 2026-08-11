package com.stano.gradle.base;

import java.util.Map;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.credentials.HttpHeaderCredentials;
import org.gradle.authentication.http.HttpHeaderAuthentication;

public class MavenRepositoryCredentials {
  private static final String STANO_MAVEN_USERNAME_PROPERTY = "com.stano.maven.username";
  private static final String STANO_MAVEN_USERNAME_ENVIRONMENT = "STANO_MAVEN_USERNAME";
  private static final String STANO_MAVEN_PASSWORD_PROPERTY = "com.stano.maven.password";
  private static final String STANO_MAVEN_PASSWORD_ENVIRONMENT = "STANO_MAVEN_PASSWORD";
  private static final String STANO_MAVEN_TOKEN_PROPERTY = "com.stano.maven.token";
  private static final String STANO_MAVEN_TOKEN_ENVIRONMENT = "STANO_MAVEN_TOKEN";
  private static final String STANO_MAVEN_TOKEN_HEADER_PROPERTY = "com.stano.maven.token-header";
  private static final String STANO_MAVEN_TOKEN_HEADER_ENVIRONMENT = "STANO_MAVEN_TOKEN_HEADER";
  private static final String DEFAULT_TOKEN_HEADER_NAME = "Private-Token";

  // GitLab CI sets this automatically for every job. When present, it always takes precedence
  // over any explicitly configured credentials, since it is short-lived and scoped to the job.
  private static final String GITLAB_CI_JOB_TOKEN_ENVIRONMENT = "CI_JOB_TOKEN";
  private static final String GITLAB_CI_JOB_TOKEN_HEADER_NAME = "Job-Token";

  public static void configureCredentials(
      Map<String, ?> properties, MavenArtifactRepository repository) {
    var ciJobToken = System.getenv(GITLAB_CI_JOB_TOKEN_ENVIRONMENT);
    if (ciJobToken != null) {
      configureHeaderCredentials(repository, GITLAB_CI_JOB_TOKEN_HEADER_NAME, ciJobToken);
      return;
    }

    var token = resolve(properties, STANO_MAVEN_TOKEN_PROPERTY, STANO_MAVEN_TOKEN_ENVIRONMENT);
    if (token != null) {
      var headerName =
          resolve(
              properties, STANO_MAVEN_TOKEN_HEADER_PROPERTY, STANO_MAVEN_TOKEN_HEADER_ENVIRONMENT);
      configureHeaderCredentials(
          repository, headerName != null ? headerName : DEFAULT_TOKEN_HEADER_NAME, token);
      return;
    }

    configurePasswordCredentials(properties, repository);
  }

  private static void configureHeaderCredentials(
      MavenArtifactRepository repository, String headerName, String token) {
    var credentials = repository.getCredentials(HttpHeaderCredentials.class);
    credentials.setName(headerName);
    credentials.setValue(token);
    repository.authentication(auth -> auth.create("header", HttpHeaderAuthentication.class));
  }

  private static void configurePasswordCredentials(
      Map<String, ?> properties, MavenArtifactRepository repository) {
    var username =
        resolve(properties, STANO_MAVEN_USERNAME_PROPERTY, STANO_MAVEN_USERNAME_ENVIRONMENT);
    var password =
        resolve(properties, STANO_MAVEN_PASSWORD_PROPERTY, STANO_MAVEN_PASSWORD_ENVIRONMENT);
    var credentials = repository.getCredentials(PasswordCredentials.class);
    credentials.setUsername(username);
    credentials.setPassword(password);
  }

  private static String resolve(
      Map<String, ?> properties, String propertyName, String environmentVariable) {
    return properties.containsKey(propertyName)
        ? properties.get(propertyName).toString()
        : System.getenv(environmentVariable);
  }
}
