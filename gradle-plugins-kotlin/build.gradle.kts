plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("kotlin") {
      id = "com.stano.kotlin"
      implementationClass = "com.stano.gradle.kotlin.KotlinPlugin"
      displayName = "Kotlin Plugin"
      description = "Opt-in Kotlin JVM support for com.stano.java subprojects. " +
        "Applies the Kotlin JVM Gradle plugin and configures KotlinCompile tasks " +
        "(incremental compilation, same lint/warning suppression free-compiler-args as the Java compiler). " +
        "Requires com.stano.java to be applied to the subproject."
      tags = listOf("convention", "kotlin", "jvm")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-java"))
  implementation(project(":gradle-plugins-base"))

  implementation(libs.kotlin.jvm.plugin)

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
