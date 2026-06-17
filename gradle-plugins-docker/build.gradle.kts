plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("docker") {
      id = "com.stano.docker"
      implementationClass = "com.stano.gradle.docker.DockerPlugin"
      displayName = "Docker Plugin"
      description = "Docker image build, tag, and push support. " +
        "Registers docker (build), dockerTag, dockerPush, dockerLogin, dockerLogout, " +
        "dockerClean, and dockerCleanupImage tasks. " +
        "Configure images via the docker DSL: image name, Dockerfile path, build args, labels, " +
        "platforms, and additional build context files. " +
        "Supports docker buildx (default) with multi-platform builds and automatic AWS ECR login. " +
        "Automatically stamps images with build provenance labels " +
        "(branch, commit hash, build number, repository URL). " +
        "When com.stano.spring-boot is also applied, auto-configures the image name " +
        "and wires bootWar output as the build context."
      tags = listOf("convention", "docker")
    }
    create("dockerCompose") {
      id = "com.stano.docker-compose"
      implementationClass = "com.stano.gradle.docker.DockerComposePlugin"
      displayName = "Docker Compose Plugin"
      description = "Docker Compose file generation and lifecycle management. " +
        "Registers generateDockerCompose (resolves artifact versions into a docker-compose.yml template), " +
        "dockerComposeUp, and dockerComposeDown tasks. " +
        "Configure the template path, output file, and token replacements via the dockerCompose DSL. " +
        "Template tokens use {{group:name}} syntax and are resolved from the docker configuration " +
        "or explicit templateToken(key, value) entries."
      tags = listOf("convention", "docker", "docker-compose")
    }
    create("dockerRun") {
      id = "com.stano.docker-run"
      implementationClass = "com.stano.gradle.docker.DockerRunPlugin"
      displayName = "Docker Run Plugin"
      description = "Docker container run lifecycle management. " +
        "Registers dockerRun, dockerStop, dockerRemoveContainer, dockerRunStatus, " +
        "and dockerNetworkModeStatus tasks. " +
        "Configure the container via the dockerRun DSL: image, container name, ports, volumes, " +
        "environment variables, command, network mode, and extra arguments. " +
        "Supports daemonized containers (default) and ephemeral containers via clean=true (adds --rm)."
      tags = listOf("convention", "docker")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))
  implementation(libs.guava)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
