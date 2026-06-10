plugins {
  id("java-gradle-plugin")
}

gradlePlugin {
  isAutomatedPublishing = false
}

dependencies {
  implementation(project(":gradle-plugins-util"))

  implementation(libs.spring.boot.plugin)

  testImplementation(project(":gradle-plugins-java-module"))
  testImplementation(project(":gradle-plugins-project"))
  testImplementation(project(":gradle-plugins-test"))

//  testImplementation(libs.spring.boot.plugin)
}
