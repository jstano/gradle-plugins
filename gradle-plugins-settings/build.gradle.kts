import com.stano.buildlogic.fullDependency

dependencies {
  implementation(project(":gradle-plugins-util"))

  implementation(fullDependency("com.github.burrunan.s3cache:s3-build-cache"))

  testImplementation(project(":gradle-plugins-test"))
}
