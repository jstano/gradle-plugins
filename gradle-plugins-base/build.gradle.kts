plugins {
  id("java-gradle-plugin")
  id("java-test-fixtures")
}

gradlePlugin {
  isAutomatedPublishing = false
}

dependencies {
  api(libs.bundles.jackson)
  api(libs.jgit)
  api(libs.snakeyaml)

  testFixturesApi(libs.bundles.junit)
  testFixturesApi(libs.mockito.core)

  testImplementation(libs.bundles.junit)
}
