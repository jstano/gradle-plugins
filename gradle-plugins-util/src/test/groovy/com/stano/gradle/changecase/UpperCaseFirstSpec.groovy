package com.stano.gradle.changecase

import spock.lang.Specification

class UpperCaseFirstSpec extends Specification {

   def "upperCaseFirst"() {

      expect:
      UpperCaseFirst.upperCaseFirst(text) == result

      where:
      text   | result
      null   | null
      ""     | ""
      "test" | "Test"
      "TEST" | "TEST"
   }
}
