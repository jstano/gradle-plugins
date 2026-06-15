plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("application") {
      id = "com.stano.application"
      implementationClass = "com.stano.gradle.application.ApplicationPlugin"
      displayName = "Application Plugin"
      description = "Extends com.stano.base for application builds. Sets project.version from ProjectVersionProvider and applies jacoco to the root."
      tags = listOf("convention", "application", "jacoco")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
