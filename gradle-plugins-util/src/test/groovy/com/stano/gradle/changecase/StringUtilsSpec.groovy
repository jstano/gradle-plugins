package com.stano.gradle.changecase

import spock.lang.Specification

class StringUtilsSpec extends Specification {

   def "isBlank should work"() {

      expect:
      StringUtils.isBlank(text) == expectedResult

      where:
      text    | expectedResult
      null    | true
      ""      | true
      " "     | true
      " abc " | false
   }
}
