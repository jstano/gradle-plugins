plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("javaLibrary") {
      id = "com.stano.java-library"
      implementationClass = "com.stano.gradle.javalibrary.JavaLibraryPlugin"
      displayName = "Java Library Plugin"
      description = "Extends com.stano.java for publishable library subprojects. " +
        "Adds javadoc and sources JARs and configures Maven publishing " +
        "to the private stano-maven repository (URL from Gradle properties or " +
        "STANO_MAVEN_URL; credentials from STANO_MAVEN_TOKEN for HTTP header auth, " +
        "or STANO_MAVEN_USERNAME / STANO_MAVEN_PASSWORD for HTTP Basic auth). " +
        "Publishing is silently skipped when STANO_MAVEN_URL is not configured. " +
        "Suppresses enforced-platform Gradle Module Metadata validation warnings."
      tags = listOf("convention", "java", "library", "publishing")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-java"))
  implementation(project(":gradle-plugins-base"))

  testImplementation(testFixtures(project(":gradle-plugins-base")))
  testImplementation(project(":gradle-plugins-maven-central-publish"))
}