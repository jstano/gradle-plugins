plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("library") {
      id = "com.stano.library"
      implementationClass = "com.stano.gradle.library.LibraryPlugin"
      displayName = "Library Plugin"
      description = "Extends com.stano.base for multi-module library builds. Applies base and jacoco to the root project."
      tags = listOf("convention", "library", "jacoco")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
