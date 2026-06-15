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
      description = "Extends com.stano.java with javadoc + sources JARs and Maven publishing for library submodules."
      tags = listOf("convention", "java", "library", "publishing")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-java"))
  implementation(project(":gradle-plugins-base"))

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
