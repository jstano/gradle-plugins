import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.authentication.http.HttpHeaderAuthentication

plugins {
  alias(libs.plugins.sonarqube)
  id("java-library")
  id("maven-publish")
  id("jacoco")
  id("com.diffplug.spotless") version libs.versions.spotless
}

sonar {
  val extraProperties = extensions.extraProperties.properties
  val sonarHost = extraProperties["com.stano.sonar.host.url"]?.toString() ?: System.getenv("STANO_SONAR_HOST_URL")
  val sonarToken = extraProperties["com.stano.sonar.token"]?.toString() ?: System.getenv("STANO_SONAR_TOKEN")

  properties {
    property("sonar.host.url", sonarHost)
    property("sonar.token", sonarToken)
    property("sonar.projectName", "gradle-plugins")
    property("sonar.projectKey", "${project.group}:gradle-plugins")
    property("sonar.projectVersion", project.version)
  }
}

val antDep = libs.ant
val junitPlatformLauncherDep = libs.junit.platform.launcher

subprojects {
  val properties = extensions.extraProperties.properties

  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  apply(plugin = "jacoco")
  apply(plugin = "com.diffplug.spotless")

  extensions.configure(com.diffplug.gradle.spotless.SpotlessExtension::class.java) {
    java {
      googleJavaFormat(libs.versions.google.java.format.get())
        .reflowLongStrings()
        .formatJavadoc(true)
      endWithNewline()
      expandWildcardImports()
      importOrder()
      removeUnusedImports()
      trimTrailingWhitespace()
    }
  }

  tasks.named("check") {
    dependsOn("spotlessCheck")
  }

  dependencies {
    implementation(gradleApi())
    implementation(antDep)
    testRuntimeOnly(junitPlatformLauncherDep)
  }

  configure<PublishingExtension> {
    val projProperties = extensions.extraProperties.properties
    val mavenUrl = projProperties["com.stano.maven.url"]?.toString() ?: System.getenv("STANO_MAVEN_URL")
    val mavenUsername = projProperties["com.stano.maven.username"]?.toString() ?: System.getenv("STANO_MAVEN_USERNAME")
    val mavenPassword = projProperties["com.stano.maven.password"]?.toString() ?: System.getenv("STANO_MAVEN_PASSWORD")

    repositories {
      maven {
        name = "stano-maven"
        url = uri(mavenUrl!!)
        credentials {
          username = mavenUsername
          password = mavenPassword
        }
        authentication {
          create<HttpHeaderAuthentication>("header")
        }
      }
    }
    publications {
      val jarTask = tasks.findByName("jar") as? org.gradle.jvm.tasks.Jar ?: return@publications
      create<MavenPublication>(jarTask.archiveBaseName.get()) {
        from(components["java"])
        artifactId = jarTask.archiveBaseName.get()
      }
    }
  }

  tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs = compilerOptions()
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    jvmArgs("--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED")
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
