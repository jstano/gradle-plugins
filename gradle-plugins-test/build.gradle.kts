dependencies {
  api(project(":gradle-plugins-util"))

  api(localGroovy())
  api(libs.byte.buddy)
  api(libs.bundles.junit)
  api(libs.spock.core)
}
