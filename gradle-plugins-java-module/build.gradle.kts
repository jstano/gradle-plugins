import com.stano.buildlogic.fullDependency

dependencies {
  implementation(project(":gradle-plugins-java-common"))
  implementation(project(":gradle-plugins-project"))
  implementation(project(":gradle-plugins-util"))

  implementation(fullDependency("org.barfuin.gradle.jacocolog:org.barfuin.gradle.jacocolog.gradle.plugin"))
  implementation(fullDependency("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin"))

  testImplementation(project(":gradle-plugins-test"))
}
