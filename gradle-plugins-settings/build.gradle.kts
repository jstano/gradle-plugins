plugins {
  id("java-gradle-plugin")
}

gradlePlugin {
  isAutomatedPublishing = false
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  implementation(libs.s3.build.cache)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
