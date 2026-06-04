package com.stano.gradle.changecase

import spock.lang.Specification

class SnakeCaseSpec extends Specification {

   def "snakeCase"() {

      expect:
      SnakeCase.snakeCase(text) == result

      where:
      text             | result
      null             | null
      ""               | ""
      "_id"            | "id"
      "test"           | "test"
      "test string"    | "test_string"
      "Test String"    | "test_string"
      "TestV2"         | "test_v2"
      "version 1.2.10" | "version_1_2_10"
      "version 1.21.0" | "version_1_21_0"
   }
}
