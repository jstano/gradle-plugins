package com.stano.gradle.changecase

import spock.lang.Specification

class HeaderCaseSpec extends Specification {

   def "headerCase"() {

      expect:
      HeaderCase.headerCase(text) == result

      where:
      text             | result
      null             | null
      ""               | ""
      "test"           | "Test"
      "test string"    | "Test-String"
      "Test String"    | "Test-String"
      "TestV2"         | "Test-V2"
      "version 1.2.10" | "Version-1-2-10"
      "version 1.21.0" | "Version-1-21-0"
   }
}
