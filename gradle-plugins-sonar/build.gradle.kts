dependencies {
  implementation(project(":gradle-plugins-util"))

  implementation(libs.sonarqube.plugin)

  testImplementation(project(":gradle-plugins-java-module"))
  testImplementation(project(":gradle-plugins-project"))
  testImplementation(project(":gradle-plugins-test"))
}
