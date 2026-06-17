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
      description = "Core plugin for internal Java/Kotlin subprojects. " +
        "Applies java-library, Kotlin JVM, JaCoCo, and Spotless (Google Java Format). " +
        "Configures the Java toolchain from the root BaseExtension's javaVersion (default '21'). " +
        "Configures tests with JUnit Platform and finalizes them with jacocoTestReport. " +
        "When mspVersion is set, enforces the MSP BOM, excludes commons-logging and log4j, " +
        "and auto-wires the MapStruct annotation processor when mapstruct is detected on the classpath. " +
        "Requires com.stano.base (or com.stano.application) on the root project."
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
