import com.stano.buildlogic.getFullDependency

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation(project(":gradle-plugins-project"))
  implementation(project(":gradle-plugins-util"))
  implementation(getFullDependency("com.google.guava:guava"))

  testImplementation(project(":gradle-plugins-test"))
}
