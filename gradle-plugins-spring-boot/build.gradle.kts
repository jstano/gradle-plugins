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
      description = "Applies org.springframework.boot and wires the MSP Spring Boot BOM for dependency management. " +
        "Names the bootJar after the root project and registers copyOtelJavaagent " +
        "to copy the OpenTelemetry Java agent into build/libs at assemble time. " +
        "Adds spring-boot-devtools (developmentOnly), micrometer-registry-prometheus (runtimeOnly), " +
        "msp-spring-boot-application (implementation), and msp-spring-test-starter (testImplementation). " +
        "Rewrites application.yml at processResources time with build metadata " +
        "(context name, version, CI environment). " +
        "When com.stano.docker is also applied, auto-configures the Docker image name, " +
        "build args, and wires bootWar output as the Docker build context."
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
