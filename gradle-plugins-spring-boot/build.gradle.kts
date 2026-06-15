plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("springBoot") {
      id = "com.stano.spring-boot"
      implementationClass = "com.stano.gradle.springboot.SpringBootPlugin"
      displayName = "Spring Boot Plugin"
      description = "Applies Spring Boot, pins Spring Boot + MSP BOM, names the boot JAR after the root project, and registers a copyOtelJavaagent task."
      tags = listOf("convention", "spring-boot", "java")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  implementation(libs.spring.boot.plugin)

  testImplementation(project(":gradle-plugins-java"))
  testImplementation(testFixtures(project(":gradle-plugins-base")))

//  testImplementation(libs.spring.boot.plugin)
}
