dependencies {
  implementation(project(":gradle-plugins-util"))

  implementation(libs.s3.build.cache)

  testImplementation(project(":gradle-plugins-test"))
}
