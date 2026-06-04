package com.stano.gradle.project

import com.stano.gradle.RootExtension
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ApplicationPluginSpec extends Specification {
  def "the application plugin should create the RootExtension and set the version for all projects to be the same as the root project"() {
    def rootProject = ProjectBuilder.builder().withName("root").build()
    def childProject = ProjectBuilder.builder().withName("child").withParent(rootProject).build();

    rootProject.setVersion("1.2.3")
    rootProject.extensions.extraProperties.set("useSemVer", "true")
    rootProject.apply(plugin: "com.stano.application")

    expect:
    rootProject.extensions.findByType(RootExtension)

    rootProject.version == "1.2.3"
    childProject.version == "1.2.3"
  }
}
