import com.stano.buildlogic.MavenRepositoryUtil
import com.stano.buildlogic.fullDependency

plugins {
  id("org.sonarqube")
  id("java-gradle-plugin")
  id("groovy")
  id("maven-publish")
  id("jacoco")
}

sonar {
  val extraProperties = extensions.extraProperties.properties
  val stanoSonarHost = extraProperties["com.stano.sonar.host.url"]?.toString() ?: System.getenv("STANO_SONAR_HOST_URL")
  val stanoSonarToken = extraProperties["com.stano.sonar.token"]?.toString() ?: System.getenv("STANO_SONAR_TOKEN")

  properties {
    property("sonar.host.url", stanoSonarHost)
    property("sonar.token", stanoSonarToken)
    property("sonar.projectName", "gradle-plugins")
    property("sonar.projectKey", "${project.group}:gradle-plugins")
    property("sonar.projectVersion", project.version)
  }
}

subprojects {
  val properties = extensions.extraProperties.properties

  apply(plugin = "java-gradle-plugin")
  apply(plugin = "groovy")
  apply(plugin = "maven-publish")
  apply(plugin = "jacoco")

  gradlePlugin {
    isAutomatedPublishing = false
  }

  dependencies {
    implementation("org.codehaus.groovy:groovy-all:${properties["groovyVersion"]}")
    implementation(fullDependency("org.apache.ant:ant"))
    implementation(gradleApi())
    testRuntimeOnly(fullDependency("org.junit.platform:junit-platform-launcher"))
  }

  MavenRepositoryUtil.configurePublishing(this)

  tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs = compilerOptions()
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  tasks.withType<GroovyCompile>().configureEach {
    options.compilerArgs = compilerOptions()
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED")
    finalizedBy(tasks.jacocoTestReport)
  }
  tasks.getByName<JacocoReport>("jacocoTestReport") {
    reports {
      html.required.set(true)
      xml.required.set(true)
    }
  }
}

fun compilerOptions(): List<String> = listOf("-Xlint:deprecation", "-nowarn", "-parameters")
