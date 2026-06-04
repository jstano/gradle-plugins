package com.stano.gradle

import com.stano.gradle.plugin.test.BasePluginSpec
import spock.lang.Ignore

@Ignore
class GradlePluginsSpec extends BasePluginSpec {
   def "verify that all plugins load properly"() {
      rootProject.apply plugin: 'stano-project'
      childProject.apply plugin: 'stano-java-module'
      childProject.apply plugin: 'stano-java-sonar'
//      childProject.apply plugin: 'stano-spring-boot-web-app'

      expect:
      rootProject.plugins.hasPlugin('stano-project')

      childProject.plugins.hasPlugin('stano-java-module')
      childProject.plugins.hasPlugin('stano-java-sonar')
   }
}
