import com.stano.buildlogic.fullDependency

dependencies {
  implementation(project(":gradle-plugins-util"))

  testImplementation(project(":gradle-plugins-java-module"))
  testImplementation(project(":gradle-plugins-project"))
  testImplementation(project(":gradle-plugins-test"))

  testImplementation(fullDependency("org.springframework.boot:org.springframework.boot.gradle.plugin"))
}
