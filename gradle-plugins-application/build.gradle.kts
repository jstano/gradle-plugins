plugins {
  id("java-gradle-plugin")
}

gradlePlugin {
  isAutomatedPublishing = false
}

dependencies {
  implementation(project(":gradle-plugins-project"))
  implementation(project(":gradle-plugins-util"))

  implementation(libs.owasp.dependency.check)

  testImplementation(project(":gradle-plugins-test"))
}
