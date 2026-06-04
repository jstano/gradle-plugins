dependencies {
  implementation(project (":gradle-plugins-project"))
  implementation(project (":gradle-plugins-util"))

  implementation(libs.kotlin.jvm.plugin)

  testImplementation(project(":gradle-plugins-project"))
  testImplementation(project(":gradle-plugins-test"))
}
