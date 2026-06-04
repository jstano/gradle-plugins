dependencies {
  api(libs.bundles.jackson)
  api(libs.jgit)
  api(libs.snakeyaml)

  testImplementation(project(":gradle-plugins-test"))
}
