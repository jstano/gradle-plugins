package com.stano.gradle.springboot

import com.stano.gradle.RootExtensionFeature
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SpringBootPluginSpec extends Specification {
  def "the spring-boot plugin should configure things properly"() {
    def project = ProjectBuilder.builder().build()
    project.ext.javaPlatformVersion = "1.0.0"
    new RootExtensionFeature().apply(project)
    project.apply(plugin: "java")
    project.apply(plugin: "com.stano.spring-boot")

    expect:
    project.plugins.hasPlugin("org.springframework.boot")
  }
}
