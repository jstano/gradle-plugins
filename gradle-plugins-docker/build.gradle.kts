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
      description = "Docker build support."
      tags = listOf("convention", "docker")
    }
    create("dockerCompose") {
      id = "com.stano.docker-compose"
      implementationClass = "com.stano.gradle.docker.DockerComposePlugin"
      displayName = "Docker Compose Plugin"
      description = "Docker Compose support."
      tags = listOf("convention", "docker", "docker-compose")
    }
    create("dockerRun") {
      id = "com.stano.docker-run"
      implementationClass = "com.stano.gradle.docker.DockerRunPlugin"
      displayName = "Docker Run Plugin"
      description = "Docker run support."
      tags = listOf("convention", "docker")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))
  implementation(libs.guava)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
