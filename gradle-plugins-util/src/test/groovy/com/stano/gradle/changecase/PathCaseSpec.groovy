package com.stano.gradle.changecase

import spock.lang.Specification

class PathCaseSpec extends Specification {

   def "pathCase"() {

      expect:
      PathCase.pathCase(text) == result

      where:
      text             | result
      null             | null
      ""               | ""
      "test"           | "test"
      "test string"    | "test/string"
      "Test String"    | "test/string"
      "TestV2"         | "test/v2"
      "version 1.2.10" | "version/1/2/10"
      "version 1.21.0" | "version/1/21/0"
   }
}
