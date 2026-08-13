dependencies {
  implementation(project(":gradle-plugins-application"))
  implementation(project(":gradle-plugins-maven-central-publish"))
  implementation(project(":gradle-plugins-docker"))
  implementation(project(":gradle-plugins-java-library"))
  implementation(project(":gradle-plugins-java"))
  implementation(project(":gradle-plugins-kotlin"))
  implementation(project(":gradle-plugins-base"))
  implementation(project(":gradle-plugins-library"))
  implementation(project(":gradle-plugins-npm"))
  implementation(project(":gradle-plugins-schema"))
  implementation(project(":gradle-plugins-settings"))
  implementation(project(":gradle-plugins-sonar"))
  implementation(project(":gradle-plugins-spring-boot"))

  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
