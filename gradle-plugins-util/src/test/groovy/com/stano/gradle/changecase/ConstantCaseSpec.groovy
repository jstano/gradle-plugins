package com.stano.gradle.changecase

import spock.lang.Specification

class ConstantCaseSpec extends Specification {
  def "constantCase"() {
    expect:
    ConstantCase.constantCase(text) == result

    where:
    text                   | result
    null                   | null
    ""                     | ""
    "test"                 | "TEST"
    "test string"          | "TEST_STRING"
    "Test String"          | "TEST_STRING"
    "dot.case"             | "DOT_CASE"
    "path/case"            | "PATH_CASE"
    "TestV2"               | "TEST_V2"
    "version 1.2.10"       | "VERSION_1_2_10"
    "version 1.21.0"       | "VERSION_1_21_0"
    "stano.sonar.host.url" | "STANO_SONAR_HOST_URL"
    "stano.sonar.token"    | "STANO_SONAR_TOKEN"
  }
}
