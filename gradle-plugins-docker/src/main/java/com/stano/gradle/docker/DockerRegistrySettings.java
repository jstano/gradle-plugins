package com.stano.gradle.docker;

import org.gradle.api.Project;

public class DockerRegistrySettings {
  private static final String DOCKER_REGISTRY_HOST_PROPERTY = "com.stano.docker.registry.host";
  private static final String DOCKER_REGISTRY_HOST_USERNAME = "com.stano.docker.registry.username";
  private static final String DOCKER_REGISTRY_HOST_PASSWORD = "com.stano.docker.registry.password";
  private static final String DOCKER_REGISTRY_HOST = "STANO_DOCKER_REGISTRY_HOST";
  private static final String DOCKER_REGISTRY_USERNAME = "STANO_DOCKER_REGISTRY_USERNAME";
  private static final String DOCKER_REGISTRY_PASSWORD = "STANO_DOCKER_REGISTRY_PASSWORD";
  private final String host;
  private final String username;
  private final String password;

  public DockerRegistrySettings(Project project) {
    final var properties = project.getExtensions().getExtraProperties().getProperties();
    host =
        properties.containsKey(DOCKER_REGISTRY_HOST_PROPERTY)
            ? properties.get(DOCKER_REGISTRY_HOST_PROPERTY).toString()
            : System.getenv(DOCKER_REGISTRY_HOST);
    username =
        properties.containsKey(DOCKER_REGISTRY_HOST_USERNAME)
            ? properties.get(DOCKER_REGISTRY_HOST_USERNAME).toString()
            : System.getenv(DOCKER_REGISTRY_USERNAME);
    password =
        properties.containsKey(DOCKER_REGISTRY_HOST_PASSWORD)
            ? properties.get(DOCKER_REGISTRY_HOST_PASSWORD).toString()
            : System.getenv(DOCKER_REGISTRY_PASSWORD);
  }

  public String getHost() {
    return host;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
