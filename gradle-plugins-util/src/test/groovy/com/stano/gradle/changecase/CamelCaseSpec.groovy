package com.stano.gradle.changecase

import spock.lang.Specification

class CamelCaseSpec extends Specification {

   def "camelCase"() {

      expect:
      CamelCase.camelCase(text) == result

      where:
      text             | result
      null             | null
      ""               | ""
      "test"           | "test"
      "test string"    | "testString"
      "Test String"    | "testString"
      "TestV2"         | "testV2"
      "_foo_bar_"      | "fooBar"
      "version 1.2.10" | "version_1_2_10"
      "version 1.21.0" | "version_1_21_0"
   }
}
