plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("settings") {
      id = "com.stano.settings"
      implementationClass = "com.stano.gradle.settings.SettingsPlugin"
      displayName = "Settings Plugin"
      description = "Settings-level plugin. Configures dependency resolution management, S3 build cache, and pins Kotlin JVM plugin version."
      tags = listOf("convention", "settings", "dependency-management")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  implementation(libs.s3.build.cache)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}

tasks.processResources {
  inputs.property("version", project.version)
  filesMatching("**/stano-plugins.properties") {
    expand("version" to project.version)
  }
}
