plugins {
  id("java-gradle-plugin")
}

gradlePlugin {
  isAutomatedPublishing = false
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  implementation(libs.jacocolog)
  implementation(libs.kotlin.jvm.plugin)
  implementation(libs.spotless)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
