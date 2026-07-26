plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("mavenCentralPublish") {
      id = "com.stano.maven-central-publish"
      implementationClass = "com.stano.gradle.mavencentralpublish.MavenCentralPublishPlugin"
      displayName = "Maven Central Publish Plugin"
      description = "Adds Maven Central Portal publishing support: POM metadata, GPG signing, " +
        "a staging-deploy publication, and a publishToMavenCentral upload task. " +
        "Composable alongside private-repo publishing (e.g. com.stano.java-library). " +
        "All POM/developer/license/component values are configured explicitly via the " +
        "'mavenCentralPublish' extension — no org-wide defaults are assumed."
      tags = listOf("publishing", "maven-central", "signing")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
