package com.stano.buildlogic

val dependencyList = listOf(
  "com.fasterxml.jackson.core:jackson-core:2.22.0",
  "com.fasterxml.jackson.core:jackson-databind:2.22.0",
  "com.github.burrunan.s3cache:s3-build-cache:1.9.5",
  "com.google.guava:guava:33.6.0-jre",
  "net.bytebuddy:byte-buddy:1.18.10",
  "org.apache.ant:ant:1.10.17",
  "org.barfuin.gradle.jacocolog:org.barfuin.gradle.jacocolog.gradle.plugin:3.1.0",
  "org.codehaus.groovy:groovy-all:3.0.25",
  "org.eclipse.jgit:org.eclipse.jgit:7.6.0.202603022253-r",
  "org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.4.0",
  "org.junit.jupiter:junit-jupiter:6.1.0",
  "org.junit.platform:junit-platform-launcher:6.1.0",
  "org.owasp:dependency-check-gradle:10.0.3",
  "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:6.2.0.5505",
  "org.spockframework:spock-core:2.4-groovy-3.0",
  "org.springframework.boot:org.springframework.boot.gradle.plugin:4.0.6",
  "org.yaml:snakeyaml:2.6",
)

fun fullDependency(dependency: String): String {
  return dependencyList.find { it.startsWith(dependency) }
    ?: throw IllegalArgumentException("Dependency $dependency not found")
}
