package com.stano.gradle.sonar

import org.gradle.testfixtures.ProjectBuilder
import org.sonarqube.gradle.SonarExtension
import spock.lang.Specification

class SonarPluginSpec extends Specification {
  def "the sonar plugin should not be applied if the com.stano.sonar.host.url settings is true"() {
    def project = ProjectBuilder.builder().build()
    project.getExtensions().getExtraProperties().set("com.stano.sonar.disabled", "true")

    project.apply(plugin: "com.stano.sonar")

    expect:
    !project.plugins.hasPlugin("org.sonarqube")
  }

  def "the sonar plugin should not be applied if the com.stano.sonar.host settings is not set"() {
    def project = ProjectBuilder.builder().build()

    project.apply(plugin: "com.stano.sonar")

    expect:
    !project.plugins.hasPlugin("org.sonarqube")
  }

  def "the sonar plugin should not be applied if the com.stano.sonar.token settings is not set"() {
    def project = ProjectBuilder.builder().build()
    project.getExtensions().getExtraProperties().set("com.stano.sonar.host", "http://localhost:9000")

    project.apply(plugin: "com.stano.sonar")

    expect:
    !project.plugins.hasPlugin("org.sonarqube")
  }

  def "the sonar plugin should be applied if the settings are valid"() {
    def project = ProjectBuilder.builder().build()
    project.getExtensions().getExtraProperties().set("com.stano.sonar.host", "http://localhost:9000")
    project.getExtensions().getExtraProperties().set("com.stano.sonar.token", "sonar")

    project.apply(plugin: "com.stano.sonar")
    project.getExtensions().getByType(SonarExtension)

    expect:
    project.plugins.hasPlugin("org.sonarqube")
  }
}
