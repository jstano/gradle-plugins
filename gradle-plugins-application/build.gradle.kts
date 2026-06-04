import com.stano.buildlogic.fullDependency

dependencies {
  implementation(project(":gradle-plugins-project"))
  implementation(project(":gradle-plugins-util"))

  implementation(fullDependency("org.owasp:dependency-check-gradle"))

  testImplementation(project(":gradle-plugins-test"))
}
