import com.stano.buildlogic.fullDependency

dependencies {
  api(project(":gradle-plugins-util"))

  api(fullDependency("net.bytebuddy:byte-buddy"))
  api(fullDependency("org.codehaus.groovy:groovy-all"))
  api(fullDependency("org.junit.jupiter:junit-jupiter"))
  api(fullDependency("org.junit.platform:junit-platform-launcher"))
  api(fullDependency("org.spockframework:spock-core"))
}
