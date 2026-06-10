plugins {
  id("java-gradle-plugin")
}

gradlePlugin {
  isAutomatedPublishing = false
}

dependencies {
  implementation(project(":gradle-plugins-java-common"))
  implementation(project(":gradle-plugins-project"))
  implementation(project(":gradle-plugins-util"))

  implementation(libs.jacocolog)
  implementation(libs.kotlin.jvm.plugin)

  testImplementation(project(":gradle-plugins-test"))
}
