plugins {
  id("java-gradle-plugin")
  id("java-test-fixtures")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("base") {
      id = "com.stano.base"
      implementationClass = "com.stano.gradle.base.BasePlugin"
      displayName = "Base Plugin"
      description = "Root-project prerequisite. Registers BaseExtension and adds jacocoRootReport. Must be applied to the root project before any other stano plugin."
      tags = listOf("convention", "jacoco", "java")
    }
  }
}

dependencies {
  api(libs.bundles.jackson)
  api(libs.jgit)
  api(libs.snakeyaml)

  testFixturesApi(libs.bundles.junit)
  testFixturesApi(libs.mockito.core)

  testImplementation(libs.bundles.junit)
}
