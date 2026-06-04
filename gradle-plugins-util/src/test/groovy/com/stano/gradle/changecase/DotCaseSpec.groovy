package com.stano.gradle.changecase

import spock.lang.Specification

class DotCaseSpec extends Specification {

   def "dotCase"() {

      expect:
      DotCase.dotCase(text) == result

      where:
      text             | result
      null             | null
      ""               | ""
      "test"           | "test"
      "test string"    | "test.string"
      "Test String"    | "test.string"
      "dot.case"       | "dot.case"
      "path/case"      | "path.case"
      "TestV2"         | "test.v2"
      "version 1.2.10" | "version.1.2.10"
      "version 1.21.0" | "version.1.21.0"
   }
}
