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
      description = "Settings-level plugin applied in settings.gradle. " +
        "Configures dependency resolution management with mavenLocal(), mavenCentral(), " +
        "and an optional private stano-maven repository. " +
        "Configures local build cache and optional S3 remote build cache " +
        "(enabled via STANO_BUILD_CACHE_TYPE=s3; configured via STANO_BUILD_CACHE_S3_* variables). " +
        "Pins the Kotlin JVM plugin to a fixed version and self-pins all com.stano plugins " +
        "to the version bundled with this plugin. " +
        "Exposes a buildCacheSettings DSL to customize the S3 cache key prefix."
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
