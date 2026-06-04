package com.stano.gradle.changecase

import spock.lang.Specification

class PascalCaseSpec extends Specification {

   def "pascalCase"() {

      expect:
      PascalCase.pascalCase(text) == result

      where:
      text             | result
      null             | null
      ""               | ""
      "test"           | "Test"
      "test string"    | "TestString"
      "Test String"    | "TestString"
      "TestV2"         | "TestV2"
      "version 1.2.10" | "Version_1_2_10"
      "version 1.21.0" | "Version_1_21_0"
   }
}
