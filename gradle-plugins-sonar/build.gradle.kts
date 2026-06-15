plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("sonar") {
      id = "com.stano.sonar"
      implementationClass = "com.stano.gradle.sonar.SonarPlugin"
      displayName = "Sonar Plugin"
      description = "SonarQube integration. Silently skips with a warning when host/token are unconfigured."
      tags = listOf("convention", "sonarqube", "code-quality")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  implementation(libs.sonarqube.plugin)

  testImplementation(project(":gradle-plugins-java"))
  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
