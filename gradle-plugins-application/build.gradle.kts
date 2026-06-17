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
      description = "Extends com.stano.base for multi-module application builds. " +
        "Applies the base and jacoco plugins to the root project. " +
        "Automatically derives project.version from git metadata: " +
        "'<commitTimestamp>-<commitHash>' (or '<commitTimestamp>-<commitHash>-<buildNumber>' in CI). " +
        "Inherits the 'root' BaseExtension and the jacocoRootReport aggregate coverage task."
      tags = listOf("convention", "application", "jacoco")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
