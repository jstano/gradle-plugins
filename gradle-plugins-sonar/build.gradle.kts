plugins {
  id("java-gradle-plugin")
}

gradlePlugin {
  isAutomatedPublishing = false
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  implementation(libs.sonarqube.plugin)

  testImplementation(project(":gradle-plugins-java"))
  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
