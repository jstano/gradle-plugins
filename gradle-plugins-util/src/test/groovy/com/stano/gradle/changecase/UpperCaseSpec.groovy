package com.stano.gradle.changecase

import spock.lang.Specification

class UpperCaseSpec extends Specification {

   def "upperCase"() {

      expect:
      UpperCase.upperCase(text) == result

      where:
      text          | result
      null          | null
      ""            | ""
      "test"        | "TEST"
      "test string" | "TEST STRING"
      "Test String" | "TEST STRING"
      "\u0131"      | "I"
   }
}
