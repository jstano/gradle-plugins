dependencies {
  implementation(project(":gradle-plugins-project"))
  implementation(project(":gradle-plugins-util"))
  implementation(libs.guava)

  testImplementation(project(":gradle-plugins-test"))
}
