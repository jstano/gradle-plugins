dependencies {
  implementation(project(":gradle-plugins-util"))

  implementation(libs.owasp.dependency.check)

  testImplementation(project(":gradle-plugins-test"))
}
