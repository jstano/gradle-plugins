package com.stano.gradle.docker;

import org.gradle.api.Project;

public class DockerRegistrySettings {
  private static final String R365_DOCKER_REGISTRY_HOST_PROPERTY = "com.r365.docker.registry.host";
  private static final String R365_DOCKER_REGISTRY_HOST_USERNAME = "com.r365.docker.registry.username";
  private static final String R365_DOCKER_REGISTRY_HOST_PASSWORD = "com.r365.docker.registry.password";
  private static final String R365_DOCKER_REGISTRY_HOST = "R365_DOCKER_REGISTRY_HOST";
  private static final String R365_DOCKER_REGISTRY_USERNAME = "R365_DOCKER_REGISTRY_USERNAME";
  private static final String R365_DOCKER_REGISTRY_PASSWORD = "R365_DOCKER_REGISTRY_PASSWORD";

  private final String host;
  private final String username;
  private final String password;

  public DockerRegistrySettings(Project project) {
    final var properties = project.getExtensions().getExtraProperties().getProperties();
    host = properties.containsKey(R365_DOCKER_REGISTRY_HOST_PROPERTY)
           ? properties.get(R365_DOCKER_REGISTRY_HOST_PROPERTY).toString()
           : System.getenv(R365_DOCKER_REGISTRY_HOST);
    username = properties.containsKey(R365_DOCKER_REGISTRY_HOST_USERNAME)
               ? properties.get(R365_DOCKER_REGISTRY_HOST_USERNAME).toString()
               : System.getenv(R365_DOCKER_REGISTRY_USERNAME);
    password = properties.containsKey(R365_DOCKER_REGISTRY_HOST_PASSWORD)
               ? properties.get(R365_DOCKER_REGISTRY_HOST_PASSWORD).toString()
               : System.getenv(R365_DOCKER_REGISTRY_PASSWORD);
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
