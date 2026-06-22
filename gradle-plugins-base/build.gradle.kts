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
      description = "Root-project prerequisite for all com.stano plugins. " +
        "Registers the 'root' BaseExtension exposing cross-project configuration: " +
        "javaVersion (default '21'), contextName, mspVersion, Docker registry coordinates, " +
        "Pact Broker coordinates, and lazy CI metadata providers (branch name, commit hash, " +
        "commit timestamp, build number). " +
        "Registers the jacocoRootReport task that aggregates JaCoCo coverage across all subprojects. " +
        "Must be applied to the root project before any other com.stano plugin."
      tags = listOf("convention", "jacoco", "java")
    }
  }
}

dependencies {
  api(libs.bundles.jackson)
  api(libs.jgit)
  api(libs.snakeyaml)
  implementation(libs.spotless)
  implementation(libs.kotlin.jvm.plugin)

  testFixturesApi(libs.bundles.junit)
  testFixturesApi(libs.mockito.core)

  testImplementation(libs.bundles.junit)
}
