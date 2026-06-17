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
      description = "SonarQube analysis integration. " +
        "Conditionally applies org.sonarqube and configures sonar.host.url, sonar.token, " +
        "sonar.projectKey (as '<group>:<name>'), sonar.projectName, and sonar.projectVersion. " +
        "Silently skips (no sonarqube task is registered) when STANO_SONAR_HOST_URL " +
        "or STANO_SONAR_TOKEN are not configured. " +
        "Optionally enforces quality gate with build failure via com.stano.sonar.fail-build-enabled."
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
