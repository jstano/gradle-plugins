import com.stano.buildlogic.fullDependency

dependencies {
  implementation(project (":gradle-plugins-project"))
  implementation(project (":gradle-plugins-util"))

  implementation(fullDependency("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin"))

  testImplementation(project(":gradle-plugins-project"))
  testImplementation(project(":gradle-plugins-test"))
}
