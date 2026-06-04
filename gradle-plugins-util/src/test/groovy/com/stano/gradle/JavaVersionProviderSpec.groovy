package com.stano.gradle

import org.gradle.api.JavaVersion
import spock.lang.Specification

class JavaVersionProviderSpec extends Specification {

   def "currentVersion should return the current java version"() {

      expect:
      new JavaVersionProvider().currentVersion() == JavaVersion.current()
   }
}
