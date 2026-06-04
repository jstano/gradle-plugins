dependencies {
  implementation(project(":gradle-plugins-util"))

  testImplementation(project(":gradle-plugins-java-module"))
  testImplementation(project(":gradle-plugins-project"))
  testImplementation(project(":gradle-plugins-test"))

  testImplementation(libs.spring.boot.plugin)
}
