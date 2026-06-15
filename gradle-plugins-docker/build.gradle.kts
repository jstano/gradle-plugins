plugins {
  id("java-gradle-plugin")
}

gradlePlugin {
  isAutomatedPublishing = false
}

dependencies {
  implementation(project(":gradle-plugins-base"))
  implementation(libs.guava)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
