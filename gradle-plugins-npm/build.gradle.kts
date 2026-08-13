plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("npm") {
      id = "com.stano.npm"
      implementationClass = "com.stano.gradle.npm.NpmPlugin"
      displayName = "Npm Plugin"
      description = "Generic npm/Node build lifecycle support, usable on any npm project " +
        "(standalone SPA, library, or embedded frontend) regardless of whether a Java plugin " +
        "is applied. Registers npmVersion, npmInstall, npmClean (wired into clean), " +
        "npmRunBuild, and npmTest tasks. Optionally builds via nvm (Node Version Manager) " +
        "instead of a system-installed npm. When org.sonarqube is already applied (directly " +
        "or on a parent project), configures sonar.sources for the JS source tree."
      tags = listOf("convention", "npm", "node", "frontend")
    }
    create("npmResources") {
      id = "com.stano.npm-resources"
      implementationClass = "com.stano.gradle.npm.NpmResourcesPlugin"
      displayName = "Npm Resources Plugin"
      description = "Embeds an npm build's output into a Java jar's resources. Extends " +
        "com.stano.npm (auto-applied) with an npmAssemble task that copies the npm dist " +
        "output into build/resources/main/public. When the java plugin is also applied, " +
        "jar depends on npmAssemble and test depends on npmTest."
      tags = listOf("convention", "npm", "node", "frontend")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))
  implementation(libs.sonarqube.plugin)
  implementation(libs.jackson.databind)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
