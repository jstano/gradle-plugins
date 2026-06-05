package com.stano.gradle.project

import com.stano.gradle.RootExtension
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ProjectPluginSpec extends Specification {
  def "the project plugin should add the RootExtension"() {
    def project = ProjectBuilder.builder().build()

    project.apply(plugin: "com.stano.project")

    expect:
    project.extensions.findByType(RootExtension)
  }
}
