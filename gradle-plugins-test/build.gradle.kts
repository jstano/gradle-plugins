dependencies {
  api(project(":gradle-plugins-util"))

  api(libs.byte.buddy)
  api("org.codehaus.groovy:groovy-all:${libs.versions.groovy.get()}")
  api(libs.bundles.junit)
  api(libs.spock.core)
}
