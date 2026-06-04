package com.stano.gradle

import spock.lang.Specification

class EnvironmentProviderSpec extends Specification {

   def "the default environment should be the SystemEnvironment"() {

      expect:
      EnvironmentProvider.environment instanceof SystemEnvironment
   }

   def "should be able to set a different Environment object"() {

      EnvironmentProvider.environment = Mock(Environment) {
         getEnvironmentVariable('TEST') >> 'test_value'
      }

      expect:
      EnvironmentProvider.environment.getEnvironmentVariable('TEST') == 'test_value'

      cleanup:
      EnvironmentProvider.reset()
   }

   def "call the private constructor so code coverage is accurate"() {

      expect:
      new EnvironmentProvider()
   }
}
