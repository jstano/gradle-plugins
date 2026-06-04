import com.stano.buildlogic.fullDependency

dependencies {
  implementation(project(":gradle-plugins-util"))

  implementation(fullDependency("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin"))

  testImplementation(project(":gradle-plugins-java-module"))
  testImplementation(project(":gradle-plugins-project"))
  testImplementation(project(":gradle-plugins-test"))
}
