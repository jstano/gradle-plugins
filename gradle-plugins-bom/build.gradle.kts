dependencies {
  implementation(project(":gradle-plugins-application"))
//  implementation(project(":gradle-plugins-docker"))
  implementation(project(":gradle-plugins-java-common"))
  implementation(project(":gradle-plugins-java-library"))
  implementation(project(":gradle-plugins-java-module"))
  implementation(project(":gradle-plugins-project"))
  implementation(project(":gradle-plugins-settings"))
  implementation(project(":gradle-plugins-sonar"))
//  implementation(project(":gradle-plugins-spring-boot"))

  testImplementation(project(":gradle-plugins-test"))
}
