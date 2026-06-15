dependencies {
  implementation(project(":gradle-plugins-application"))
//  implementation(project(":gradle-plugins-docker"))
  implementation(project(":gradle-plugins-java-library"))
  implementation(project(":gradle-plugins-java"))
  implementation(project(":gradle-plugins-base"))
  implementation(project(":gradle-plugins-settings"))
  implementation(project(":gradle-plugins-sonar"))
  implementation(project(":gradle-plugins-spring-boot"))

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
