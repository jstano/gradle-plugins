import com.stano.buildlogic.fullDependency

dependencies {
  api(fullDependency("com.fasterxml.jackson.core:jackson-core"))
  api(fullDependency("com.fasterxml.jackson.core:jackson-databind"))
  api(fullDependency("org.eclipse.jgit:org.eclipse.jgit"))
  api(fullDependency("org.yaml:snakeyaml"))

  testImplementation(project(":gradle-plugins-test"))
}
