plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("java") {
      id = "com.stano.java"
      implementationClass = "com.stano.gradle.java.JavaPlugin"
      displayName = "Java Plugin"
      description = "Core plugin for internal Java/Kotlin modules. Applies java-library, Kotlin JVM, JaCoCo, and Spotless."
      tags = listOf("convention", "java", "kotlin", "jacoco", "spotless")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  implementation(libs.jacocolog)
  implementation(libs.kotlin.jvm.plugin)
  implementation(libs.spotless)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
